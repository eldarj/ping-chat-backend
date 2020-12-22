package com.pingchat.authenticationservice.data.mysql.repository;

import com.pingchat.authenticationservice.data.mysql.entity.DSNodeEntity;
import com.pingchat.authenticationservice.data.mysql.entity.MessageEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface DataSpaceNodeRepository extends JpaRepository<DSNodeEntity, Long>  {
    @Query("SELECT ds FROM DSNodeEntity ds WHERE " +
            "(ds.ownerId = :userId AND ds.receiverId = :anotherUserId) OR " +
            "(ds.ownerId = :anotherUserId AND ds.receiverId = :userId)")
    List<DSNodeEntity> findSharedDataByUsers(Long userId, Long anotherUserId);
}
