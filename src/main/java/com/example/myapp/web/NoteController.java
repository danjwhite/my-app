package com.example.myapp.web;

import com.example.myapp.domain.Note;
import com.example.myapp.domain.User;
import com.example.myapp.service.INoteService;
import com.example.myapp.service.ISecurityService;
import com.example.myapp.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.List;

@Controller
@RequestMapping("/notes")
public class NoteController {

    private ISecurityService securityService;

    private IUserService userService;

    private INoteService noteService;

    @Autowired
    public NoteController(ISecurityService securityService, IUserService userService, INoteService noteService) {
        this.securityService = securityService;
        this.userService = userService;
        this.noteService = noteService;
    }

    public NoteController() {
    }

    @ModelAttribute
    public User user() {
        UserDetails principal = securityService.getPrincipal();
        return userService.findByUsername(principal.getUsername());
    }

    @RequestMapping(value = "/view/entries", method = RequestMethod.GET)
    public String getNotes(@RequestParam(value = "display", required = false) String display,
                           @CookieValue(value = "displayCookie", required = false) String displayCookieValue,
                           @ModelAttribute("user") User user,
                           Model model,
                           HttpServletResponse response) {

        // Determine the display type.
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

        // Get notes based on the display type.
        List<Note> notes = display.equals("recent") ? noteService.findRecent() :
                noteService.findAll();

        // Add attributes to the model.
        model.addAttribute("notes", notes);
        model.addAttribute("display", display);

        return "notes";
    }

    @RequestMapping(value = "/view/entry", method = RequestMethod.GET)
    public String getNote(@RequestParam(value = "noteId") long noteId, @ModelAttribute("user") User user, Model model) {
        model.addAttribute("note", noteService.findById(noteId));

        return "note";
    }

    @RequestMapping(value = "/add", method = RequestMethod.GET)
    public String addNote(@ModelAttribute("user") User user, Model model) {
        model.addAttribute("note", new Note());
        model.addAttribute("formType", "add");

        return "noteForm";
    }

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public String saveNote(@Valid Note note, BindingResult result, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "noteForm";
        }

        Long noteId = noteService.add(note).getId();
        redirectAttributes.addAttribute("noteId", noteId);
        redirectAttributes.addAttribute("confirmation", "added");
        return "redirect:/notes/view/entry";
    }
    
    @RequestMapping(value = "/edit", method = RequestMethod.GET)
    public String editNote(@RequestParam(value = "noteId") long noteId, @ModelAttribute("user") User user, Model model) {
        Note note = noteService.findById(noteId);
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
