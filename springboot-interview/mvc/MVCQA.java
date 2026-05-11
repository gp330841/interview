package springboot.interview.mvc;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

/**
 * Spring Boot - Spring MVC Interview Questions
 * Contains practical code examples for MVC Controller logic, PRG pattern, and Form binding.
 */
public class MVCQA {

    // Dummy DTO for examples
    public static class UserDto {
        @NotEmpty(message = "Name cannot be empty")
        private String name;
        
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
    }

    @Controller
    @RequestMapping("/users")
    public static class UserController {

        // Q9: What is the Model interface used for? [Easy]
        public void q9() {
            /*
             * Answer: To pass data from the Controller to the View rendering engine.
             */
        }
        @GetMapping("/profile")
        public String showProfile(Model model) {
            model.addAttribute("username", "JohnDoe");
            return "profile-page"; // Resolves to profile-page.html
        }

        // Q16: Difference between Model, ModelMap, and ModelAndView? [Medium]
        public void q16() {
            /*
             * Answer: ModelAndView holds both the model data and the logical view name.
             */
        }
        @GetMapping("/dashboard")
        public ModelAndView showDashboard() {
            ModelAndView mav = new ModelAndView("dashboard");
            mav.addObject("stats", "Some statistics data");
            return mav;
        }

        // Q13 & Q14: Form Submission, @ModelAttribute, and BindingResult [Medium]
        public void q13_q14() {
            /*
             * Answer: @ModelAttribute binds form inputs to a DTO. BindingResult holds validation errors.
             * Note: BindingResult must immediately follow the @Valid parameter.
             */
        }
        @PostMapping("/register")
        public String processRegistration(@Valid @ModelAttribute UserDto userDto, 
                                          BindingResult bindingResult, 
                                          Model model) {
            if (bindingResult.hasErrors()) {
                // If validation fails, return the user to the form page to see errors
                return "register-form"; 
            }
            
            // Save user to database...
            return "redirect:/users/success";
        }

        // Q19 & Q20: Prevent double form submission (PRG Pattern) and RedirectAttributes [Medium]
        public void q19_q20() {
            /*
             * Answer: Use "redirect:/path" to prevent double submission (PRG). 
             * Use RedirectAttributes.addFlashAttribute to pass data securely across the redirect.
             */
        }
        @PostMapping("/update")
        public String updateProfile(UserDto userDto, RedirectAttributes redirectAttributes) {
            // Logic to update profile...
            
            // This message survives the redirect and is available on the next page
            redirectAttributes.addFlashAttribute("successMessage", "Profile updated successfully!");
            
            // Forces browser to make a new GET request
            return "redirect:/users/profile"; 
        }

        // Q17: How do you handle file uploads? [Medium]
        public void q17() {
            /*
             * Answer: Use the MultipartFile interface as a method parameter.
             */
        }
        @PostMapping("/upload-avatar")
        public String handleFileUpload(@RequestParam("file") MultipartFile file) {
            if (!file.isEmpty()) {
                System.out.println("Uploading file: " + file.getOriginalFilename());
                // file.getBytes() or file.transferTo(destFile)
            }
            return "redirect:/users/profile";
        }
    }
}
