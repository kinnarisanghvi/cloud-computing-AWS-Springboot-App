package com.csye6225.spring2019.repository;

import com.csye6225.spring2019.model.Attachment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;



@Repository
public interface AttachmentRepository extends JpaRepository<Attachment, String> {




}
