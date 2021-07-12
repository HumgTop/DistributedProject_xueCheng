package com.xuecheng.order.dao;

import com.github.pagehelper.Page;
import com.xuecheng.framework.domain.task.XcTask;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;

/**
 * @Autor HumgTop
 * @Date 2021/7/9 22:18
 * @Version 1.0
 */
@Repository
public interface XcTaskRepository extends JpaRepository<XcTask, String> {
    /**
     * @param pageable   分页查询条件对象
     * @param updateTime
     * @return
     */
    Page<XcTask> findByUpdateTimeBefore(Pageable pageable, Date updateTime);

    //更新updateTime
    @Modifying
    @Query("update XcTask t set t.updateTime = :updateTime where t.id= :id")
    //jpa的SQL语句是面向对象的
    int updateTaskTime(@Param("id") String id, @Param("updateTime") Date updateTime);

    //update语句返回int型数据
    //更新版本号，如果返回结果大于0，说明更新成功，可以继续进行下一步操作
    @Modifying
    @Query("update XcTask t set t.version = :version + 1 where t.id = :id and t.version = :version")
    int updateTaskVersion(@Param("id") String id, @Param("version") int version);
}
