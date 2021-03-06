package com.example.myapp.web.controller;

import com.example.myapp.domain.Note;
import com.example.myapp.domain.User;
import com.example.myapp.dto.NoteDto;
import com.example.myapp.service.NoteService;
import com.example.myapp.service.UserService;
import com.example.myapp.web.ResponseFactory;
import com.example.myapp.web.RestResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.List;

@Controller
@SessionAttributes("userInContext")
@RequestMapping("/notes")
public class NoteController {

    private final UserService userService;
    private final NoteService noteService;

    public NoteController(UserService userService, NoteService noteService) {
        this.userService = userService;
        this.noteService = noteService;
    }

    @ModelAttribute("userInContext")
    public User user() {
        return userService.getLoggedInUser();
    }

    @GetMapping
    public String getNotes(@RequestParam(value = "display", required = false) String display,
                           @CookieValue(value = "displayCookie", required = false) String displayCookieValue,
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

    @GetMapping(value = "/{id}")
    @ResponseBody
    public ResponseEntity<NoteDto> getNote(@PathVariable(value = "id") long id) {
        return new ResponseEntity<>(new NoteDto(noteService.findById(id)), HttpStatus.OK);
    }

    @PostMapping
    @ResponseBody
    public ResponseEntity<RestResponse> saveNote(@RequestBody @Valid NoteDto noteDto, BindingResult result) {
        if (result.hasErrors()) {
            return ResponseFactory.error(result);
        }

        noteService.add(noteDto);

        return ResponseFactory.success(HttpStatus.CREATED);
    }

    @PutMapping
    @ResponseBody
    public ResponseEntity<RestResponse> updateNote(@RequestBody @Valid NoteDto noteDto, BindingResult result) {
        if (result.hasErrors()) {
            return ResponseFactory.error(result);
        }

        noteService.update(noteDto);

        return ResponseFactory.success(HttpStatus.CREATED);
    }

    @DeleteMapping(value = "/{id}")
    @ResponseBody
    public ResponseEntity<RestResponse> deleteNote(@PathVariable(value = "id") long id) {
        noteService.delete(noteService.findById(id));

        return ResponseFactory.success(HttpStatus.OK);
    }
}
