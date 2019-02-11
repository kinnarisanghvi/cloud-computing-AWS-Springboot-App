package com.csye6225.spring2019.controller;

import com.csye6225.spring2019.exception.ResourceNotFoundException;
import com.csye6225.spring2019.model.Note;
import com.csye6225.spring2019.model.User;
import com.csye6225.spring2019.repository.NoteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
public class NoteController {

    @Autowired
    NoteRepository noteRepository;


    @GetMapping("/note")
    public List<Note> getAllNote() {

        return noteRepository.findAll();
    }

    @PostMapping("/note")
    public Note newNote(@Valid @RequestBody Note note) {


        return noteRepository.save(note);
    }

    @GetMapping("/note/{id}")
    public Note getOneNote(@PathVariable(value = "noteid") Long noteid) {

        return noteRepository.findById(noteid).orElseThrow(() -> new ResourceNotFoundException("Note", "noteid", noteid));
    }

    @PutMapping("/note/{id}")
    public Note updateNote(@PathVariable(value = "noteid") Long noteid, @Valid @RequestBody Note note) {

        Note note1 = noteRepository.findById(noteid).orElseThrow(() -> new ResourceNotFoundException("Note", "noteid", noteid));


        note1.setNoteTitle(note.getNoteTitle());
        note1.setNoteContent(note.getNoteContent());

        Note changedNote = noteRepository.save(note1);
        return changedNote;


    }


    @DeleteMapping("/note/{id}")
    public ResponseEntity<?> deleteNote(@PathVariable(value = "noteid") Long noteid) {
        Note note1 = noteRepository.findById(noteid).orElseThrow(() -> new ResourceNotFoundException("Note", "noteid", noteid));
        noteRepository.delete(note1);
        return ResponseEntity.ok().build();

    }
}