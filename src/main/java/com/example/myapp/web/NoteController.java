package com.example.myapp.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.myapp.service.INoteService;
import com.example.myapp.domain.Note;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.List;

@Controller
@RequestMapping("/notes")
public class NoteController {

    private INoteService noteService;

    @Autowired
    public NoteController(INoteService noteService) {
        this.noteService = noteService;
    }

    public NoteController() {
    }

    @RequestMapping(value = "/view/entries", method = RequestMethod.GET)
    public String getNotes(@RequestParam(value = "display", required = false) String display,
                           @CookieValue(value = "displayCookie", required = false) String displayCookieValue,
                           Model model,
                           HttpServletResponse response) {

        if (display == null) {
            if (displayCookieValue == null) {
                display = "recent";
            } else {
                display = displayCookieValue;
            }
        } else {
            Cookie cookie = new Cookie("displayCookie", display);
            response.addCookie(cookie);
        }

        List<Note> notes = display.equals("recent") ? noteService.findRecent() :
                noteService.findAll();

        model.addAttribute("notes", notes);
        model.addAttribute("display", display);

        return "notes";
    }

    @RequestMapping(value = "/view/entry", method = RequestMethod.GET)
    public String getNote(@RequestParam(value = "noteId") long noteId, Model model) {
        model.addAttribute("note", noteService.findOne(noteId));

        return "note";
    }

    @RequestMapping(value = "/add", method = RequestMethod.GET)
    public String addNote(Model model) {
        model.addAttribute("note", new Note());
        model.addAttribute("formType", "add");

        return "noteForm";
    }

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public String saveNote(@Valid Note note, RedirectAttributes redirectAttributes, Errors errors) {
        if (errors.hasErrors()) {
            return "noteForm";
        }

        Long noteId = noteService.add(note).getId();
        redirectAttributes.addAttribute("noteId", noteId);
        redirectAttributes.addAttribute("confirmation", "added");
        return "redirect:/notes/view/entry";
    }
    
    @RequestMapping(value = "/edit", method = RequestMethod.GET)
    public String editNote(@RequestParam(value = "noteId") long noteId, Model model) {
        Note note = noteService.findOne(noteId);
        model.addAttribute("note", note);
        model.addAttribute("formType", "edit");

        return "noteForm";
    }

    @RequestMapping(value = "/edit", method = RequestMethod.POST)
    public String updateNote(@Valid Note note, @RequestParam(value = "noteId") long noteId, RedirectAttributes redirectAttributes, Errors errors) {
        if (errors.hasErrors()) {
            return "noteForm";
        }

        noteService.update(note);
        redirectAttributes.addAttribute("noteId", noteId);
        redirectAttributes.addAttribute("confirmation", "edited");
        return "redirect:/notes/view/entry";
    }
    
    @RequestMapping(value = "/delete", method = RequestMethod.GET)
    public String deleteNote(@RequestParam(value = "noteId") long noteId) {
        noteService.delete(noteId);

        return "redirect:/notes/view/entries";
    }
}
