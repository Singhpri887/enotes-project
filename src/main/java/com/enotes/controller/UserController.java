package com.enotes.controller;

import com.enotes.entity.Notes;
import com.enotes.entity.User;
import com.enotes.repository.UserRepository;
import com.enotes.service.NotesService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDate;

@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private NotesService notesService;

    @ModelAttribute
    public User getUser(Principal p, Model m) {
        String email = p.getName();
        User user = userRepo.findByEmail(email);
        m.addAttribute("user", user);
        return user;
    }

    @GetMapping("/addNotes")
    public String addNotes(Model m) {
        m.addAttribute("activeLink", "addNotes");
        return "add_notes";
    }

    @GetMapping("/viewNotes")
    public String viewNotes(Model m, Principal p, @RequestParam(defaultValue = "0") Integer pageNo) {
        User user = getUser(p, m);
        Page<Notes> notes = notesService.getNotesByUserAndCategory(user, "NOTE", pageNo);
        m.addAttribute("currentPage", pageNo);
        m.addAttribute("totalElements", notes.getTotalElements());
        m.addAttribute("totalPages", notes.getTotalPages());
        m.addAttribute("notesList", notes.getContent());
        m.addAttribute("activeLink", "viewNotes");
        return "view_notes";
    }

    @GetMapping("/editNotes/{id}")
    public String editNotes(@PathVariable int id, Model m) {
        Notes notes = notesService.getNotesById(id);
        m.addAttribute("n", notes);
        return "edit_notes";
    }

    @PostMapping("/saveNotes")
    public String saveNotes(@ModelAttribute Notes notes,
            HttpSession session,
            Principal principal) {
        try {
            User user = userRepo.findByEmail(principal.getName());
            notes.setUser(user);
            // notes.setDate(LocalDate.now()); // Moved logic below

            if (notes.getCategory() == null || notes.getCategory().isEmpty()) {
                notes.setCategory("NOTE");
            }

            // For categories that use the date field from the form (DATES), we must not
            // overwrite it if present.
            // For others, set to now if null.
            if (notes.getDate() == null) {
                notes.setDate(LocalDate.now());
            }

            Notes savedNotes = notesService.saveNotes(notes);

            if (savedNotes != null) {
                session.setAttribute("msg", "Saved successfully");
            } else {
                session.setAttribute("msg", "Failed to save");
            }
        } catch (Exception e) {
            session.setAttribute("msg", "Error: " + e.getMessage());
        }

        // Redirect based on category
        String category = notes.getCategory();
        if ("DIARY".equals(category))
            return "redirect:/user/diary";
        if ("TODO".equals(category))
            return "redirect:/user/todo";
        if ("GOAL".equals(category))
            return "redirect:/user/goals";
        if ("IDEA".equals(category))
            return "redirect:/user/ideas";
        if ("QUOTE".equals(category))
            return "redirect:/user/quotes";
        if ("QA".equals(category))
            return "redirect:/user/qa";
        if ("DATES".equals(category))
            return "redirect:/user/dates";
        if ("SCHEDULE".equals(category))
            return "redirect:/user/schedule";
        if ("ROUTINE".equals(category))
            return "redirect:/user/routine";

        return "redirect:/user/addNotes";
    }

    @PostMapping("/updateNotes")
    public String updateNotes(@ModelAttribute Notes notes, HttpSession session, Principal principal) {
        try {
            User user = userRepo.findByEmail(principal.getName());
            notes.setUser(user);
            // notes.setDate(LocalDate.now()); // Moved logic below

            // Preserve category if editing
            Notes existing = notesService.getNotesById(notes.getId());
            if (existing != null) {
                notes.setCategory(existing.getCategory());
            }

            if (notes.getDate() == null) {
                notes.setDate(LocalDate.now());
            }

            Notes savedNotes = notesService.saveNotes(notes);

            if (savedNotes != null) {
                session.setAttribute("msg", "Update successfully");
            } else {
                session.setAttribute("msg", "Failed to update");
            }
        } catch (Exception e) {
            session.setAttribute("msg", "Error: " + e.getMessage());
        }

        // Redirect based on category (need to fetch it again or store it)
        String category = notes.getCategory();
        if ("DIARY".equals(category))
            return "redirect:/user/diary";
        if ("TODO".equals(category))
            return "redirect:/user/todo";
        if ("GOAL".equals(category))
            return "redirect:/user/goals";
        if ("IDEA".equals(category))
            return "redirect:/user/ideas";
        if ("QUOTE".equals(category))
            return "redirect:/user/quotes";
        if ("QA".equals(category))
            return "redirect:/user/qa";
        if ("DATES".equals(category))
            return "redirect:/user/dates";
        if ("SCHEDULE".equals(category))
            return "redirect:/user/schedule";
        if ("ROUTINE".equals(category))
            return "redirect:/user/routine";

        return "redirect:/user/viewNotes";
    }

    @GetMapping("/deleteNotes/{id}")
    public String deleteNotes(@PathVariable int id, HttpSession session) {
        boolean f = notesService.deleteNotes(id);
        if (f) {
            session.setAttribute("msg", "Delete successfull");
        } else {
            session.setAttribute("msg", "Something wrong");
        }
        // Ideally checking referer or passing a param to redirect back to correct page
        // For now defaulting to viewNotes, but improved logic would be better.
        // Let's rely on the user to navigate back for now or implement generic
        // redirect.
        return "redirect:/user/viewNotes";
    }

    // New Features Routes

    @GetMapping("/diary")
    public String diary(Model m, Principal p, @RequestParam(defaultValue = "0") Integer pageNo) {
        User user = getUser(p, m);
        Page<Notes> notes = notesService.getNotesByUserAndCategory(user, "DIARY", pageNo);
        m.addAttribute("currentPage", pageNo);
        m.addAttribute("totalElements", notes.getTotalElements());
        m.addAttribute("totalPages", notes.getTotalPages());
        m.addAttribute("notesList", notes.getContent());
        m.addAttribute("activeLink", "diary");
        return "diary";
    }

    @GetMapping("/todo")
    public String todo(Model m, Principal p, @RequestParam(defaultValue = "0") Integer pageNo) {
        User user = getUser(p, m);
        Page<Notes> notes = notesService.getNotesByUserAndCategory(user, "TODO", pageNo);
        m.addAttribute("notesList", notes.getContent());
        m.addAttribute("currentPage", pageNo);
        m.addAttribute("totalPages", notes.getTotalPages());
        m.addAttribute("totalPages", notes.getTotalPages());
        m.addAttribute("activeLink", "todo");
        return "todo";
    }

    @GetMapping("/goals")
    public String goals(Model m, Principal p, @RequestParam(defaultValue = "0") Integer pageNo) {
        User user = getUser(p, m);
        Page<Notes> notes = notesService.getNotesByUserAndCategory(user, "GOAL", pageNo);
        m.addAttribute("notesList", notes.getContent());
        m.addAttribute("currentPage", pageNo);
        m.addAttribute("totalPages", notes.getTotalPages());
        m.addAttribute("totalPages", notes.getTotalPages());
        m.addAttribute("activeLink", "goals");
        return "goals";
    }

    @GetMapping("/ideas")
    public String ideas(Model m, Principal p, @RequestParam(defaultValue = "0") Integer pageNo) {
        User user = getUser(p, m);
        Page<Notes> notes = notesService.getNotesByUserAndCategory(user, "IDEA", pageNo);
        m.addAttribute("notesList", notes.getContent());
        m.addAttribute("currentPage", pageNo);
        m.addAttribute("totalPages", notes.getTotalPages());
        m.addAttribute("totalPages", notes.getTotalPages());
        m.addAttribute("activeLink", "ideas");
        return "ideas";
    }

    @GetMapping("/quotes")
    public String quotes(Model m, Principal p, @RequestParam(defaultValue = "0") Integer pageNo) {
        User user = getUser(p, m);
        Page<Notes> notes = notesService.getNotesByUserAndCategory(user, "QUOTE", pageNo);
        m.addAttribute("notesList", notes.getContent());
        m.addAttribute("currentPage", pageNo);
        m.addAttribute("totalPages", notes.getTotalPages());
        m.addAttribute("totalPages", notes.getTotalPages());
        m.addAttribute("activeLink", "quotes");
        return "quotes";
    }

    @GetMapping("/qa")
    public String qa(Model m, Principal p, @RequestParam(defaultValue = "0") Integer pageNo) {
        User user = getUser(p, m);
        Page<Notes> notes = notesService.getNotesByUserAndCategory(user, "QA", pageNo);
        m.addAttribute("notesList", notes.getContent());
        m.addAttribute("currentPage", pageNo);
        m.addAttribute("totalPages", notes.getTotalPages());
        m.addAttribute("totalPages", notes.getTotalPages());
        m.addAttribute("activeLink", "qa");
        return "qa";
    }

    @GetMapping("/dates")
    public String dates(Model m, Principal p, @RequestParam(defaultValue = "0") Integer pageNo) {
        User user = getUser(p, m);
        Page<Notes> notes = notesService.getNotesByUserAndCategory(user, "DATES", pageNo);
        m.addAttribute("notesList", notes.getContent());
        m.addAttribute("currentPage", pageNo);
        m.addAttribute("totalPages", notes.getTotalPages());
        m.addAttribute("activeLink", "dates");
        return "dates";
    }

    @GetMapping("/schedule")
    public String schedule(Model m, Principal p, @RequestParam(defaultValue = "0") Integer pageNo) {
        User user = getUser(p, m);
        Page<Notes> notes = notesService.getNotesByUserAndCategory(user, "SCHEDULE", pageNo);
        m.addAttribute("notesList", notes.getContent());
        m.addAttribute("currentPage", pageNo);
        m.addAttribute("totalPages", notes.getTotalPages());
        m.addAttribute("activeLink", "schedule");
        return "schedule";
    }

    @GetMapping("/routine")
    public String routine(Model m, Principal p, @RequestParam(defaultValue = "0") Integer pageNo) {
        User user = getUser(p, m);
        Page<Notes> notes = notesService.getNotesByUserAndCategory(user, "ROUTINE", pageNo);
        m.addAttribute("notesList", notes.getContent());
        m.addAttribute("currentPage", pageNo);
        m.addAttribute("totalPages", notes.getTotalPages());
        m.addAttribute("activeLink", "routine");
        return "routine";
    }

}
