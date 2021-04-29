package com.xuecheng.manage_cms.service;

import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSDownloadStream;
import com.mongodb.client.gridfs.model.GridFSFile;
import com.xuecheng.framework.domain.cms.CmsConfig;
import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.framework.domain.cms.CmsTemplate;
import com.xuecheng.framework.domain.cms.request.QueryPageRequest;
import com.xuecheng.framework.domain.cms.response.CmsCode;
import com.xuecheng.framework.domain.cms.response.CmsPageResult;
import com.xuecheng.framework.exception.ExceptionCast;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.QueryResult;
import com.xuecheng.manage_cms.dao.CmsConfigRepository;
import com.xuecheng.manage_cms.dao.CmsPageRepository;
import com.xuecheng.manage_cms.dao.CmsTemplateRepository;
import freemarker.cache.StringTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;


@Service    //业务层实现类，由Spring容器管理
public class PageService {
    CmsPageRepository cmsPageRepository;
    CmsConfigRepository cmsConfigRepository;
    CmsTemplateRepository cmsTemplateRepository;

    RestTemplate restTemplate;
    GridFsTemplate gridFsTemplate;
    GridFSBucket gridFSBucket;

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired  //使用构造方法自动注入
    public PageService(CmsPageRepository cmsPageRepository,
                       CmsConfigRepository cmsConfigRepository,
                       RestTemplate restTemplate,
                       CmsTemplateRepository cmsTemplateRepository,
                       GridFsTemplate gridFsTemplate,
                       GridFSBucket gridFSBucket) {

        this.cmsPageRepository = cmsPageRepository;
        this.cmsConfigRepository = cmsConfigRepository;
        this.restTemplate = restTemplate;
        this.cmsTemplateRepository = cmsTemplateRepository;
        this.gridFsTemplate = gridFsTemplate;
        this.gridFSBucket = gridFSBucket;
    }


    /**
     * @param page
     * @param size
     * @param queryPageRequest 查询条件
     * @return
     */
    public QueryResponseResult findList(int page, int size, QueryPageRequest queryPageRequest) {
        page--;
        if (queryPageRequest == null) {
            queryPageRequest = new QueryPageRequest();
        }

        Pageable pageable = PageRequest.of(page, size);
        //根据别名模糊查询
        ExampleMatcher matcher = ExampleMatcher.matching().withMatcher("pageAliase", ExampleMatcher.GenericPropertyMatchers.contains());
        //设置条件值对象
        CmsPage cmsPage = new CmsPage();
        String condition;   //查询条件
        condition = queryPageRequest.getPageAliase();
        cmsPage.setPageAliase(condition);

        if (StringUtils.isNotEmpty(condition = queryPageRequest.getSiteId())) {
            cmsPage.setSiteId(condition);
        }

        if (StringUtils.isNotEmpty(condition = queryPageRequest.getTemplateId())) {
            cmsPage.setTemplateId(condition);
        }

        Example<CmsPage> example = Example.of(cmsPage, matcher);

        Page<CmsPage> all = cmsPageRepository.findAll(example, pageable);    //查询后的结果集


        QueryResult<CmsPage> queryResult = new QueryResult<>();
        queryResult.setList(all.getContent());
        queryResult.setTotal(all.getTotalElements());

        return new QueryResponseResult(CommonCode.SUCCESS, queryResult);    //设置响应代码
    }

    /**
     * Dao方法从Spring Data MongoDB中继承
     *
     * @param cmsPage
     */
    public CmsPageResult add(CmsPage cmsPage) {
        if (cmsPage == null) {
            //抛出非法参数异常，指定异常信息内容

        }
        //校验页面名称、站点ID、页面WebPath的唯一性
        CmsPage res = cmsPageRepository.findByPageNameAndPageWebPathAndSiteId(cmsPage.getPageName(), cmsPage.getPageWebPath(), cmsPage.getSiteId());
        //先判断各类异常
        if (res != null) {
            //页面已经存在异常
            ExceptionCast.cast(CmsCode.CMS_ADDPAGE_EXISTSNAME);
        }

        cmsPage.setPageId(null);    //保证主键由MongoDB生成
        cmsPageRepository.insert(cmsPage);
        return new CmsPageResult(CommonCode.SUCCESS, cmsPage);
        //如果页面已存在，则返回错误代码
    }

    /**
     * 根据主键Id查询返回页面
     *
     * @param id
     * @return
     */
    public CmsPage getById(String id) {
        CmsPage cmsPage = null;
        Optional<CmsPage> optional = cmsPageRepository.findById(id);
        if (optional.isPresent()) {
            cmsPage = optional.get();
        }

        return cmsPage;
    }

    /**
     * 根据id查询cmsPage，并更新此cmsPage
     *
     * @param id
     * @param cmsPage
     * @return
     */
    public CmsPageResult update(String id, CmsPage cmsPage) {
        CmsPage target = getById(id);
        if (target != null) {
            target.setTemplateId(cmsPage.getTemplateId());
            target.setSiteId(cmsPage.getSiteId());
            target.setPageAliase(cmsPage.getPageAliase());
            target.setPageName(cmsPage.getPageName());
            target.setPageWebPath(cmsPage.getPageWebPath());
            target.setPagePhysicalPath(cmsPage.getPagePhysicalPath());
            target.setDataUrl(cmsPage.getDataUrl());
            //将target重新存入数据库
            CmsPage save = cmsPageRepository.save(target);
            return new CmsPageResult(CommonCode.SUCCESS, save);
        }

        return new CmsPageResult(CommonCode.FAIL, cmsPage);
    }

    public CmsConfig getModelById(String id) {
        CmsConfig cmsConfig = null;

        Optional<CmsConfig> optional = cmsConfigRepository.findById(id);
        if (optional.isPresent()) {
            cmsConfig = optional.get();
        }

        return cmsConfig;
    }

    /**
     * 页面静态化
     *
     * @param pageId
     * @return
     */
    public String getPageHtml(String pageId) {
        //获取数据模型
        Map model = getModelByPageId(pageId);
        if (model == null) {
            ExceptionCast.cast(CmsCode.CMS_GENERATEHTML_DATAISNULL);
        }

        //获取页面模板
        String templateContent = getTemplateId(pageId);
        if (StringUtils.isEmpty(templateContent)) {
            //页面模板为空
            ExceptionCast.cast(CmsCode.CMS_GENERATEHTML_TEMPLATEISNULL);
        }
        //执行静态化
        String html = generateHtml(templateContent, model);
        if (StringUtils.isEmpty(html)) {
            ExceptionCast.cast(CmsCode.CMS_GENERATEHTML_HTMLISNULL);
        }

        return html;
    }

    /**
     * 页面静态化
     *
     * @param template 模板
     * @param model    模型数据
     * @return 静态页面
     */
    public String generateHtml(String template, Map model) {
        try {
            //生成freemarker配置类
            Configuration configuration = new Configuration(Configuration.getVersion());
            //模板加载器
            StringTemplateLoader stringTemplateLoader = new StringTemplateLoader();
            stringTemplateLoader.putTemplate("template", template);
            //配置模板加载器
            configuration.setTemplateLoader(stringTemplateLoader);
            //获取模板
            Template template1 = configuration.getTemplate("template");
            String html = FreeMarkerTemplateUtils.processTemplateIntoString(template1, model);

            return html;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取数据模型
     *
     * @param pageId
     * @return
     */
    private Map getModelByPageId(String pageId) {
        CmsPage cmsPage = this.getById(pageId);
        if (cmsPage == null) {
            ExceptionCast.cast(CmsCode.CMS_PAGE_NOTEXISTS);
        }

        String dataUrl = cmsPage.getDataUrl();  //http请求地址（请求cmsConfig数据）

        if (StringUtils.isEmpty(dataUrl)) {
            //抛出异常
            ExceptionCast.cast(CmsCode.CMS_GENERATEHTML_DATAURLISNULL);
        }

        ResponseEntity<Map> forEntity = restTemplate.getForEntity(dataUrl, Map.class);
        return forEntity.getBody();
    }

    /**
     * 获取页面的模板信息
     *
     * @param pageId
     * @return
     */
    public String getTemplateId(String pageId) {
        CmsPage cmsPage = getById(pageId);
        if (cmsPage == null) {
            ExceptionCast.cast(CmsCode.CMS_PAGE_NOTEXISTS);
        }

        //查询模板信息
        Optional<CmsTemplate> optional = cmsTemplateRepository.findById(cmsPage.getTemplateId());


        if (optional.isPresent()) {
            CmsTemplate cmsTemplate = optional.get();
            String templateFileId = cmsTemplate.getTemplateFileId();
            //从Grid FS中取出模板文件
            GridFSFile gridFSFile = gridFsTemplate.findOne(Query.query(Criteria.where("_id").is(templateFileId)));
            //打开一个下载流对象
            GridFSDownloadStream gridFSDownloadStream = gridFSBucket.openDownloadStream(gridFSFile.getObjectId());
            //获取流
            GridFsResource gridFsResource = new GridFsResource(gridFSFile, gridFSDownloadStream);
            //从流中取数据
            try {
                String content = IOUtils.toString(gridFsResource.getInputStream(), "utf-8");
                return content;
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }


        return cmsPage.getTemplateId();
    }
}
