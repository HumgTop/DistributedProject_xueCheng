package com.xuecheng.manage_cms.service;

import com.alibaba.fastjson.JSON;
import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSDownloadStream;
import com.mongodb.client.gridfs.model.GridFSFile;
import com.xuecheng.framework.domain.cms.CmsConfig;
import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.framework.domain.cms.CmsSite;
import com.xuecheng.framework.domain.cms.CmsTemplate;
import com.xuecheng.framework.domain.cms.request.QueryPageRequest;
import com.xuecheng.framework.domain.cms.response.CmsCode;
import com.xuecheng.framework.domain.cms.response.CmsPageResult;
import com.xuecheng.framework.domain.cms.response.CmsPostPageResult;
import com.xuecheng.framework.exception.ExceptionCast;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.QueryResult;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.manage_cms.config.RabbitmqConfig;
import com.xuecheng.manage_cms.dao.CmsConfigRepository;
import com.xuecheng.manage_cms.dao.CmsPageRepository;
import com.xuecheng.manage_cms.dao.CmsSiteRepository;
import com.xuecheng.manage_cms.dao.CmsTemplateRepository;
import freemarker.cache.StringTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
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
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;


@Service    //????????????????????????Spring????????????
public class PageService {
    CmsPageRepository cmsPageRepository;
    CmsConfigRepository cmsConfigRepository;
    CmsTemplateRepository cmsTemplateRepository;
    CmsSiteRepository cmsSiteRepository;

    RestTemplate restTemplate;
    GridFsTemplate gridFsTemplate;
    GridFSBucket gridFSBucket;

    RabbitTemplate rabbitTemplate;

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired  //??????????????????????????????
    public PageService(CmsPageRepository cmsPageRepository,
                       CmsConfigRepository cmsConfigRepository,
                       RestTemplate restTemplate,
                       CmsTemplateRepository cmsTemplateRepository,
                       GridFsTemplate gridFsTemplate,
                       GridFSBucket gridFSBucket,
                       RabbitTemplate rabbitTemplate,
                       CmsSiteRepository cmsSiteRepository) {

        this.cmsPageRepository = cmsPageRepository;
        this.cmsConfigRepository = cmsConfigRepository;
        this.restTemplate = restTemplate;
        this.cmsTemplateRepository = cmsTemplateRepository;
        this.gridFsTemplate = gridFsTemplate;
        this.gridFSBucket = gridFSBucket;
        this.rabbitTemplate = rabbitTemplate;
        this.cmsSiteRepository = cmsSiteRepository;
    }


    /**
     * @param page
     * @param size
     * @param queryPageRequest ????????????
     * @return
     */
    public QueryResponseResult findList(int page, int size, QueryPageRequest queryPageRequest) {
        page--;
        if (queryPageRequest == null) {
            queryPageRequest = new QueryPageRequest();
        }

        Pageable pageable = PageRequest.of(page, size);
        //????????????????????????
        ExampleMatcher matcher = ExampleMatcher.matching().withMatcher("pageAliase", ExampleMatcher.GenericPropertyMatchers.contains());
        //?????????????????????
        CmsPage cmsPage = new CmsPage();
        String condition;   //????????????
        condition = queryPageRequest.getPageAliase();
        cmsPage.setPageAliase(condition);

        if (StringUtils.isNotEmpty(condition = queryPageRequest.getSiteId())) {
            cmsPage.setSiteId(condition);
        }

        if (StringUtils.isNotEmpty(condition = queryPageRequest.getTemplateId())) {
            cmsPage.setTemplateId(condition);
        }

        Example<CmsPage> example = Example.of(cmsPage, matcher);

        Page<CmsPage> all = cmsPageRepository.findAll(example, pageable);    //?????????????????????


        QueryResult<CmsPage> queryResult = new QueryResult<>();
        queryResult.setList(all.getContent());
        queryResult.setTotal(all.getTotalElements());

        return new QueryResponseResult(CommonCode.SUCCESS, queryResult);    //??????????????????
    }

    /**
     * Dao?????????Spring Data MongoDB?????????
     *
     * @param cmsPage
     */
    public CmsPageResult add(CmsPage cmsPage) {
        if (cmsPage == null) {
            //???????????????????????????????????????????????????

        }
        //???????????????????????????ID?????????WebPath????????????
        CmsPage res = cmsPageRepository.findByPageNameAndPageWebPathAndSiteId(cmsPage.getPageName(), cmsPage.getPageWebPath(), cmsPage.getSiteId());
        //?????????????????????
        if (res != null) {
            //????????????????????????
            ExceptionCast.cast(CmsCode.CMS_ADDPAGE_EXISTSNAME);
        }

        cmsPage.setPageId(null);    //???????????????MongoDB??????
        cmsPageRepository.insert(cmsPage);
        return new CmsPageResult(CommonCode.SUCCESS, cmsPage);
        //?????????????????????????????????????????????
    }

    /**
     * ????????????Id??????????????????
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
     * ??????id??????cmsPage???????????????cmsPage
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
            //???target?????????????????????
            CmsPage save = cmsPageRepository.save(target);
            return new CmsPageResult(CommonCode.SUCCESS, save);
        }

        return new CmsPageResult(CommonCode.FAIL, cmsPage);
    }

    public CmsConfig getModelById(String cmsConfigId) {
        CmsConfig cmsConfig = null;

        Optional<CmsConfig> optional = cmsConfigRepository.findById(cmsConfigId);
        if (optional.isPresent()) {
            cmsConfig = optional.get();
        }

        return cmsConfig;
    }

    /**
     * ???????????????(????????????+???????????????????????????
     *
     * @param pageId
     * @return
     */
    public String getPageHtml(String pageId) {
        //??????????????????
        Map model = getModelByPageId(pageId);
        if (model == null) {
            ExceptionCast.cast(CmsCode.CMS_GENERATEHTML_DATAISNULL);
        }

        //??????????????????
        String templateContent = getTemplateId(pageId);
        if (StringUtils.isEmpty(templateContent)) {
            //??????????????????
            ExceptionCast.cast(CmsCode.CMS_GENERATEHTML_TEMPLATEISNULL);
        }
        //???????????????
        String html = generateHtml(templateContent, model);
        if (StringUtils.isEmpty(html)) {
            ExceptionCast.cast(CmsCode.CMS_GENERATEHTML_HTMLISNULL);
        }

        return html;
    }

    /**
     * ???????????????
     *
     * @param template ??????
     * @param model    ????????????
     * @return ????????????
     */
    public String generateHtml(String template, Map model) {
        try {
            //??????freemarker?????????
            Configuration configuration = new Configuration(Configuration.getVersion());
            //???????????????
            StringTemplateLoader stringTemplateLoader = new StringTemplateLoader();
            stringTemplateLoader.putTemplate("template", template);
            //?????????????????????
            configuration.setTemplateLoader(stringTemplateLoader);
            //????????????
            Template template1 = configuration.getTemplate("template");
            String html = FreeMarkerTemplateUtils.processTemplateIntoString(template1, model);

            return html;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * ??????????????????
     *
     * @param pageId
     * @return
     */
    private Map getModelByPageId(String pageId) {
        CmsPage cmsPage = this.getById(pageId);
        if (cmsPage == null) {
            ExceptionCast.cast(CmsCode.CMS_PAGE_NOTEXISTS);
        }

        String dataUrl = cmsPage.getDataUrl();  //http?????????????????????cmsConfig?????????

        if (StringUtils.isEmpty(dataUrl)) {
            //????????????
            ExceptionCast.cast(CmsCode.CMS_GENERATEHTML_DATAURLISNULL);
        }

        ResponseEntity<Map> forEntity = restTemplate.getForEntity(dataUrl, Map.class);
        return forEntity.getBody();
    }

    /**
     * ??????cms_template???????????????id??????GridFS???????????????????????????????????????
     *
     * @param pageId
     * @return
     */
    public String getTemplateId(String pageId) {
        CmsPage cmsPage = getById(pageId);
        if (cmsPage == null) {
            ExceptionCast.cast(CmsCode.CMS_PAGE_NOTEXISTS);
        }

        //??????????????????
        Optional<CmsTemplate> optional = cmsTemplateRepository.findById(cmsPage.getTemplateId());


        if (optional.isPresent()) {
            CmsTemplate cmsTemplate = optional.get();
            String templateFileId = cmsTemplate.getTemplateFileId();
            //???Grid FS?????????????????????
            GridFSFile gridFSFile = gridFsTemplate.findOne(Query.query(Criteria.where("_id").is(templateFileId)));
            //???????????????????????????
            GridFSDownloadStream gridFSDownloadStream = gridFSBucket.openDownloadStream(gridFSFile.getObjectId());
            //?????????
            GridFsResource gridFsResource = new GridFsResource(gridFSFile, gridFSDownloadStream);
            //??????????????????
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

    /**
     * ????????????
     *
     * @param pageId
     * @return
     */
    public ResponseResult post(String pageId) {
        //???????????????
        String pageHtml = getPageHtml(pageId);
        if (StringUtils.isEmpty(pageHtml)) {
            ExceptionCast.cast(CmsCode.CMS_GENERATEHTML_HTMLISNULL);
        }
        //?????????????????????
        CmsPage cmsPage = saveHtml(pageId, pageHtml);
        //????????????
        sendPostPage(pageId);
        return new ResponseResult(CommonCode.SUCCESS);
    }

    /**
     * ???????????????????????????JSON?????????
     *
     * @param pageId
     */
    private void sendPostPage(String pageId) {
        Optional<CmsPage> optional = cmsPageRepository.findById(pageId);
        //if (failure) return;
        if (!optional.isPresent()) {
            ExceptionCast.cast(CmsCode.CMS_PAGE_NOTEXISTS);
        }
        CmsPage cmsPage = optional.get();
        Map<String, String> msgMap = new HashMap<>();
        msgMap.put("pageId", pageId);
        //??????json??????
        String msg = JSON.toJSONString(msgMap);
        //????????????id??????routingKey
        String routingKey = cmsPage.getSiteId();

        //??????????????????????????????????????????routinKey?????????????????????
        /**
         * param1??? ?????????
         * param2???routinKey
         * param3???????????????
         */
        rabbitTemplate.convertAndSend(RabbitmqConfig.EX_ROUTING_CMS_POSTPAGE, routingKey, msg);
    }

    /**
     * ?????????????????????GridFS???
     *
     * @param pageId
     * @param pageHtml
     * @return
     */
    private CmsPage saveHtml(String pageId, String pageHtml) {
        Optional<CmsPage> optional = cmsPageRepository.findById(pageId);
        //if (failure) return;
        if (!optional.isPresent()) {
            ExceptionCast.cast(CmsCode.CMS_PAGE_NOTEXISTS);
        }
        CmsPage cmsPage = optional.get();

        String htmlFileId = cmsPage.getHtmlFileId();
        //?????????????????????GridFS??????????????????
        if (StringUtils.isNotEmpty(htmlFileId)) {
            gridFsTemplate.delete(Query.query(Criteria.where("_id").is(htmlFileId)));
        }

        //?????????????????????GridFS??????????????????cmsPage??????PageName???
        InputStream inputStream = IOUtils.toInputStream(pageHtml);
        ObjectId objectId = gridFsTemplate.store(inputStream, cmsPage.getPageName());   //??????id
        //?????????id??????cmsPage???
        cmsPage.setHtmlFileId(objectId.toString());
        //?????????????????????MongoDB???
        cmsPageRepository.save(cmsPage);
        return cmsPage;
    }

    public CmsPageResult save(CmsPage cmsPage) {
        CmsPage cmsPageInDb = cmsPageRepository.findByPageNameAndPageWebPathAndSiteId(cmsPage.getPageName(),
                cmsPage.getPageWebPath(),
                cmsPage.getSiteId());
        if (cmsPageInDb != null) {
            //???????????????save????????????cmsPageInDb
            cmsPage.setPageId(cmsPageInDb.getPageId());
        }

        //?????????????????????????????????????????????
        cmsPageRepository.save(cmsPage);
        return new CmsPageResult(CommonCode.SUCCESS, cmsPage);
    }

    //??????????????????
    public CmsPostPageResult postPageQuick(CmsPage cmsPage) {
        //1.??????cmspage???????????????
        CmsPageResult cmsPageResult = save(cmsPage);
        String pageId = cmsPageResult.getCmsPage().getPageId();
        //2.??????????????????????????????????????????GridFS??????MQ???????????????
        ResponseResult responseResult = post(pageId);
        if (!responseResult.isSuccess()) {
            ExceptionCast.cast(CommonCode.FAIL);
        }
        String pageUrl = null;

        //??????????????????
        Optional<CmsSite> cmsSiteOptional = cmsSiteRepository.findById(cmsPage.getSiteId());
        if (cmsSiteOptional.isPresent()) {
            CmsSite cmsSite = cmsSiteOptional.get();
            pageUrl = cmsSite.getSiteDomain() + cmsSite.getSiteWebPath() + cmsPage.getPageWebPath() + cmsPage.getPageName();
        }
        if (pageUrl == null) {
            return new CmsPostPageResult(CommonCode.FAIL, null);
        }
        return new CmsPostPageResult(CommonCode.SUCCESS, pageUrl);
    }
}
