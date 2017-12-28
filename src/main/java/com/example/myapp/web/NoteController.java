package com.example.myapp.web;

import com.example.myapp.service.INoteService;
import com.sun.org.apache.regexp.internal.RE;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.myapp.domain.Note;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    @RequestMapping(value = "/entries/{filter}", method = RequestMethod.GET)
    public String getNotes(@PathVariable("filter") String filter,
                           @RequestParam(value = "count", defaultValue = "10") int count,
                           Model model) {
        List<Note> notes = filter.equals("recent") ? noteService.findRecent(count) :
                noteService.findAll();

        model.addAttribute("notes", notes);
        model.addAttribute("filter", filter);

        return "notes";
    }

    @RequestMapping(value = "/{noteId}", method = RequestMethod.GET)
    public String getNote(@PathVariable("noteId") long noteId, Model model) {
        model.addAttribute("note", noteService.findOne(noteId));

        return "note";
    }

    // TODO: Add test for this method.
    @RequestMapping(value = "/add", method = RequestMethod.GET)
    public String addNote(Model model) {
        model.addAttribute("note", new Note());
        model.addAttribute("formType", "add");

        return "noteForm";
    }

    // TODO: Add test for this method.
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public String saveNote(@Valid Note note, RedirectAttributes redirectAttributes, Errors errors) {
        if (errors.hasErrors()) {
            return "noteForm";
        }

        Long noteId = noteService.add(note).getId();
        redirectAttributes.addAttribute("confirmation", "added");
        return "redirect:/note/" + noteId;
    }

    // TODO: Add test for this method.
    @RequestMapping(value = "/edit/{noteId}", method = RequestMethod.GET)
    public String editNote(@PathVariable long noteId, Model model) {
        Note note = noteService.findOne(noteId);
        model.addAttribute("note", note);
        model.addAttribute("formType", "edit");

        return "noteForm";
    }

    // TODO: Add test for this method.
    @RequestMapping(value = "/edit/{noteId}", method = RequestMethod.POST)
    public String updateNote(@Valid Note note, @PathVariable long noteId, RedirectAttributes redirectAttributes, Errors errors) {
        if (errors.hasErrors()) {
            return "noteForm";
        }

        noteService.update(note);
        redirectAttributes.addAttribute("confirmation", "edited");
        return "redirect:/note/" + noteId;
    }

    // TODO: Add test for this method.
    @RequestMapping(value = "/delete")
    public String deleteNote(@RequestParam(value = "noteId") long noteId, HttpServletRequest request) {
        noteService.delete(noteId);

        return "redirect:/note/entries/recent";
    }
}
