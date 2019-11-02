package com.example.myapp.web.controller;

import com.example.myapp.domain.Note;
import com.example.myapp.domain.User;
import com.example.myapp.dto.NoteDto;
import com.example.myapp.web.RestResponse;
import com.example.myapp.service.NoteService;
import com.example.myapp.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    // TODO: Refactor tests
    @GetMapping(value = "/{id}")
    @ResponseBody
    public ResponseEntity<NoteDto> getNote(@PathVariable(value = "id") long id) {
        return new ResponseEntity<>(new NoteDto(noteService.findById(id)), HttpStatus.OK);
    }

    // TODO: Refactor tests
    @PostMapping
    @ResponseBody
    public ResponseEntity<RestResponse> saveNote(@RequestBody @Valid NoteDto noteDto, BindingResult result) {
        // TODO: Consolidate
        if (result.hasErrors()) {
            RestResponse restResponse = new RestResponse();
            restResponse.setHasErrors(true);
            Map<String, String> errors = result.getFieldErrors().stream()
                    .collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage));
            restResponse.setErrors(errors);

            return new ResponseEntity<>(restResponse, HttpStatus.OK);
        }

        noteService.add(noteDto);

        RestResponse restResponse = new RestResponse();
        restResponse.setHasErrors(false);

        return new ResponseEntity<>(restResponse, HttpStatus.CREATED);
    }

    // TODO: Refactor tests
    @PutMapping
    @ResponseBody
    public ResponseEntity<RestResponse> updateNote(@RequestBody @Valid NoteDto noteDto, BindingResult result) {
        // TODO: Consolidate
        if (result.hasErrors()) {
            RestResponse restResponse = new RestResponse();
            restResponse.setHasErrors(true);
            Map<String, String> errors = result.getFieldErrors().stream()
                    .collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage));
            restResponse.setErrors(errors);

            return new ResponseEntity<>(restResponse, HttpStatus.OK);
        }

        noteService.update(noteDto);

        RestResponse restResponse = new RestResponse();
        restResponse.setHasErrors(false);

        return new ResponseEntity<>(restResponse, HttpStatus.ACCEPTED);
    }

    // TODO: Refactor tests
    @DeleteMapping(value = "/{id}")
    @ResponseBody
    public ResponseEntity<RestResponse> deleteNote(@PathVariable(value = "id") long id) {
        noteService.delete(noteService.findById(id));

        RestResponse restResponse = new RestResponse();
        restResponse.setHasErrors(false);

        return new ResponseEntity<>(restResponse, HttpStatus.OK);
    }
}
