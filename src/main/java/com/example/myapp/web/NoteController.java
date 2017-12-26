package com.example.myapp.web;

import com.example.myapp.service.INoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.myapp.domain.Note;

import javax.validation.Valid;

@Controller
@RequestMapping("/note")
public class NoteController {

    private INoteService noteService;

    @Autowired
    public NoteController(INoteService noteService) {
        this.noteService = noteService;
    }

    public NoteController() {
    }

    @RequestMapping(value = "/entries/all", method = RequestMethod.GET)
    public String getNotes(Model model) {
        model.addAttribute("notes", noteService.findAll());
        model.addAttribute("filter", "all");

        return "notes";
    }

    @RequestMapping(value = "/entries/recent", method = RequestMethod.GET)
    public String getRecentNotes(Model model,
                                 @RequestParam(value = "count", defaultValue = "10") int count) {
        model.addAttribute("notes", noteService.findRecent(count));
        model.addAttribute("filter", "recent");
        return "notes";
    }

    @RequestMapping(value = "/{noteId}", method = RequestMethod.GET)
    public String getNote(@PathVariable("noteId") long noteId, Model model) {
        model.addAttribute("note", noteService.findOne(noteId));

        return "note";
    }

    @RequestMapping(value = "/add", method = RequestMethod.GET)
    public String showNoteForm(Model model) {
        model.addAttribute("note", new Note());
        model.addAttribute("formType", "add");
        return "noteForm";
    }

    // TODO: Create test for POST request for this method.
    @RequestMapping(value = {"/add", "/edit/*"}, method = RequestMethod.POST)
    public String saveNote(@Valid  Note note, Errors errors) throws Exception {
        if (errors.hasErrors()) {
            return "noteForm";
        }

        Long id = noteService.save(note).getId();

        return "redirect:/note/" + id;
    }

    // TODO: Add functionality to edit and delete a note.

    // TODO: Create test for this method.
    @RequestMapping(value = "/edit/{noteId}", method = RequestMethod.GET)
    public String editNote(@PathVariable("noteId") long noteId, Model model) {
        model.addAttribute("note", noteService.findOne(noteId));
        model.addAttribute("formType", "edit");
        return "noteForm";
    }
}
