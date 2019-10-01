package com.example.myapp.web;

import com.example.myapp.domain.Note;
import com.example.myapp.domain.User;
import com.example.myapp.dto.NoteDto;
import com.example.myapp.service.NoteService;
import com.example.myapp.service.SecurityService;
import com.example.myapp.service.UserService;
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
@RequestMapping("/")
public class NoteController {

    private SecurityService securityService;

    private UserService userService;

    private NoteService noteService;

    @Autowired
    public NoteController(SecurityService securityService, UserService userService, NoteService noteService) {
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

    @RequestMapping(value = "/notes/view", method = RequestMethod.GET)
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

    @RequestMapping(value = "/note/{noteId}/view", method = RequestMethod.GET)
    public String getNote(@PathVariable(value = "noteId") long noteId,
                          @ModelAttribute("user") User user, Model model) throws ResourceNotFoundException {
        Note note = noteService.findById(noteId);
        if (note == null) {
            throw new ResourceNotFoundException("No note found with id = " + noteId);
        }
        model.addAttribute("note", noteService.findById(noteId));

        return "note";
    }

    @RequestMapping(value = "/note/add", method = RequestMethod.GET)
    public String addNote(@ModelAttribute("user") User user, Model model) {
        model.addAttribute("noteDto", new NoteDto());
        model.addAttribute("formType", "add");

        return "noteForm";
    }

    @RequestMapping(value = "/note/add", method = RequestMethod.POST)
    public String saveNote(@Valid NoteDto noteDto, BindingResult result, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "noteForm";
        }

        Long noteId = noteService.add(noteDto).getId();
        redirectAttributes.addAttribute("confirmation", "added");

        return "redirect:/note/" + noteId + "/view";
    }

    @RequestMapping(value = "/note/{noteId}/edit", method = RequestMethod.GET)
    public String editNote(@PathVariable(value = "noteId") long noteId, @ModelAttribute("user") User user, Model model) {
        NoteDto noteDto = new NoteDto(noteService.findById(noteId));
        model.addAttribute("noteDto", noteDto);
        model.addAttribute("formType", "edit");

        return "noteForm";
    }

    @RequestMapping(value = "/note/{noteId}/edit", method = RequestMethod.POST)
    public String updateNote(@Valid NoteDto noteDto, @PathVariable(value = "noteId") long noteId, RedirectAttributes redirectAttributes, Errors errors) {
        if (errors.hasErrors()) {
            return "noteForm";
        }

        noteService.update(noteDto);
        redirectAttributes.addAttribute("confirmation", "edited");

        return "redirect:/note/" + noteId + "/view";
    }

    @RequestMapping(value = "/note/{noteId}/delete", method = RequestMethod.GET)
    public String deleteNote(@PathVariable(value = "noteId") long noteId) {
        Note note = noteService.findById(noteId);
        noteService.delete(note);

        return "redirect:/notes/view";
    }
}
