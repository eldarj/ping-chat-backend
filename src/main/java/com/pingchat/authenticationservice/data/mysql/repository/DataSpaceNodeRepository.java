package com.pingchat.authenticationservice.data.mysql.repository;

import com.pingchat.authenticationservice.data.mysql.entity.DSNodeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface DataSpaceNodeRepository extends JpaRepository<DSNodeEntity, Long> {
    // Getters
    @Query("SELECT ds FROM DSNodeEntity ds WHERE " +
            "ds.deletedForUserIds NOT LIKE CONCAT('%:', :userId, ':%') AND " +
            "(" +
            "(ds.ownerId = :userId AND ds.receiverId = :peerId) OR " +
            "(ds.ownerId = :peerId AND ds.receiverId = :userId)" +
            ")")
    List<DSNodeEntity> findSharedDataByUsers(Long userId, Long peerId);

    @Query(value = "SELECT ds.* FROM dataspace_nodes ds WHERE " +
            "ds.deleted_for_user_ids NOT LIKE CONCAT('%:', ?1, '%:') AND " +
            "(" +
            "(ds.owner_id = ?1 AND ds.receiver_id = ?2) OR " +
            "(ds.owner_id = ?2 AND ds.receiver_id = ?1)" +
            ") " +
            "ORDER BY ds.created_timestamp DESC " +
            "LIMIT ?3 OFFSET 0",
            nativeQuery = true)
    List<DSNodeEntity> findSharedDataByUsersLimit(Long userId, Long peerId, Long nodesCount);

    @Query("SELECT ds FROM DSNodeEntity ds WHERE " +
            "ds.deletedForUserIds NOT LIKE CONCAT('%:', :userId, ':%') AND " +
            "ds.parentDirectoryNodeId = :parentDirectoryNodeId")
    List<DSNodeEntity> findAllByParentDirectoryNodeId(Long parentDirectoryNodeId, Long userId);

    @Query("SELECT ds FROM DSNodeEntity ds WHERE " +
            "ds.deletedForUserIds NOT LIKE CONCAT('%:', :userId, ':%') AND " +
            "ds.parentDirectoryNodeId = :parentDirectoryNodeId")
    List<DSNodeEntity> findAllByDirectory(Long parentDirectoryNodeId, Long userId);

    List<DSNodeEntity> findAllByParentDirectoryNodeId(Long parentDirectoryNodeId);

    @Query("SELECT ds FROM DSNodeEntity ds WHERE " +
            "ds.deletedForUserIds NOT LIKE CONCAT('%:', :ownerId, ':%') AND " +
            "ds.ownerId = :ownerId AND " +
            "ds.parentDirectoryNodeId IS NULL")
    List<DSNodeEntity> findAllByOwnerIdAndParentDirectoryNodeIdIsNull(Long ownerId);

    @Query("SELECT ds FROM DSNodeEntity ds WHERE " +
            "ds.deletedForUserIds NOT LIKE CONCAT('%:', :receiverId, ':%') AND " +
            "ds.receiverId = :receiverId")
    List<DSNodeEntity> findAllByReceiverId(Long receiverId);

    // Deletes
    void deleteByUploadId(String uploadId);

    @Modifying
    @Query("UPDATE DSNodeEntity ds " +
            "SET ds.deletedForUserIds = CONCAT(ds.deletedForUserIds, ':', :userId, ':') " +
            "WHERE ds.id = :nodeId")
    void deleteForUser(Long nodeId, Long userId);
}
