package com.pingchat.authenticationservice.data.mysql.repository;

import com.pingchat.authenticationservice.data.mysql.entity.DSNodeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface DataSpaceNodeRepository extends JpaRepository<DSNodeEntity, Long> {
    @Query("SELECT ds FROM DSNodeEntity ds WHERE " +
            "ds.deletedByOwner = FALSE AND " +
            "(" +
            "(ds.ownerId = :userId AND ds.receiverId = :anotherUserId) OR " +
            "(ds.ownerId = :anotherUserId AND ds.receiverId = :userId)" +
            ")")
    List<DSNodeEntity> findSharedDataByUsers(Long userId, Long anotherUserId);

    @Query(value = "SELECT ds.* FROM dataspace_nodes ds " +
            "WHERE ds.deleted_by_owner = false AND " +
            "(" +
            "(ds.owner_id = ?1 AND ds.receiver_id = ?2) OR " +
            "(ds.owner_id = ?2 AND ds.receiver_id = ?1)" +
            ") " +
            "ORDER BY ds.created_timestamp DESC " +
            "LIMIT ?3 OFFSET 0",
            nativeQuery = true)
    List<DSNodeEntity> findSharedDataByUsersLimit(Long userId, Long anotherUserId, Long nodesCount);

    @Query("SELECT ds FROM DSNodeEntity ds WHERE ds.ownerId = :userId")
    List<DSNodeEntity> findDataByUser(Long userId);

    List<DSNodeEntity> findAllByParentDirectoryNodeId(Long parentDirectoryNodeId);

    List<DSNodeEntity> findAllByOwnerIdAndParentDirectoryNodeIdIsNull(Long ownerId);

    @Modifying
    @Query("UPDATE DSNodeEntity ds SET ds.deletedByOwner = true WHERE ds.id = :messageId")
    void setDeletedByOwner(Long messageId);

    @Modifying
    @Query("UPDATE DSNodeEntity ds SET ds.deletedByReceiver = true WHERE ds.id = :messageId")
    void setDeletedByReceiver(Long messageId);

    void deleteByUploadId(String uploadId);

    void deleteAllByParentDirectoryNodeId(Long nodeId);
}
