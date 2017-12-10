package com.example.myapp.web;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.myapp.domain.Note;
import com.example.myapp.data.NoteRepository;

import javax.validation.Valid;

@Controller
@RequestMapping("/note")
public class NoteController {

    private NoteRepository noteRepository;

    @Autowired
    public NoteController(NoteRepository noteRepository) {
        this.noteRepository = noteRepository;
    }

    @RequestMapping(value = "/entries/all", method = RequestMethod.GET)
    public String getNotes(Model model) {
        model.addAttribute("notes", noteRepository.findNotes());

        return "notes";
    }

    @RequestMapping(value = "/entries/recent", method = RequestMethod.GET)
    public String getRecentNotes(Model model,
                                 @RequestParam(value = "count", defaultValue = "20") int count) {
        model.addAttribute("notes", noteRepository.findRecentNotes(count));

        return "notes";
    }

    @RequestMapping(value = "/{noteId}",method = RequestMethod.GET)
    public String getNote(@PathVariable("noteId") long noteId, Model model) {
        model.addAttribute("note", noteRepository.findOne(noteId));

        return "note";
    }

    @RequestMapping(value = "/add", method = RequestMethod.GET)
    public String showNoteForm() {
        return "noteForm";
    }

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public String addNote(@Valid  Note note, Errors errors) throws Exception {
        if (errors.hasErrors()) {
            return "noteForm";
        }

        note.setCreatedAt(new Date());
        noteRepository.save(note);

        return "redirect:/note/" + note.getId();
    }
}
