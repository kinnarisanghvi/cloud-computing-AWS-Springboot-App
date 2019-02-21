package com.csye6225.spring2019.repository;

import com.csye6225.spring2019.model.Note;
import com.csye6225.spring2019.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NoteRepository extends JpaRepository<Note, String> {

    public default Note findBy(String id) {
        List<Note> notes = this.findAll();
        Note foundNote = null;
        for (Note note1 : notes) {
            System.out.println("noterepo "+ note1);
            if (note1.getNoteId().equals(id)){
                foundNote = note1;
                System.out.println("foundnote " + foundNote);
                break;
            }
        }
        return foundNote;
    }
}
