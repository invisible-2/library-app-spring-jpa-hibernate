package org.example.controllers;

import org.example.models.Book;
import org.example.models.Person;
import org.example.services.BooksService;
import org.example.services.PeopleService;
import org.example.util.BookValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Controller
@RequestMapping("/books")
public class BooksController {

   private final BooksService booksService;
   private final PeopleService peopleService;
    private final BookValidator bookValidator;

    @Autowired
    public BooksController(BooksService booksService, PeopleService peopleService, BookValidator bookValidator) {
        this.booksService = booksService;
        this.peopleService = peopleService;
        this.bookValidator = bookValidator;
    }

    @GetMapping()
    public String index(Model model,
                        @RequestParam(value = "page", required = false) Integer page,
                        @RequestParam(value = "books_per_page", required = false) Integer bookPerPage,
                        @RequestParam(value = "sort_by_year", required = false) boolean sortByYear) {

        if(page == null || bookPerPage == null)
            model.addAttribute("books", booksService.findAll());
        else
            model.addAttribute("books", booksService.findByPages(page, bookPerPage, sortByYear));


        return "books/index";
    }


    @GetMapping("/{id}")
    public String show(Model model, @ModelAttribute("person") Person person, @PathVariable("id") int id) {
        model.addAttribute("book", booksService.findById(id));
        model.addAttribute("people", peopleService.findAll());

        Person person1 = booksService.getOwnerByBookId(id);

        if (person1 != null){
              model.addAttribute("owner", person1.getFullName());

        }else {
            model.addAttribute("owner", "null");
        }

        return "books/show";
    }

    @GetMapping("/new")
    public String newBook(@ModelAttribute("book")Book book) {
        return "books/new";
    }

    @PostMapping()
    public String create(@ModelAttribute("book") @Valid Book book, BindingResult bindingResult) {

        bookValidator.validate(book, bindingResult);

        if(bindingResult.hasErrors())
            return "books/new";

        booksService.save(book);
        return "redirect:/books";
    }

    @GetMapping("{id}/edit")
    public String edit(Model model, @PathVariable("id") int id) {
        model.addAttribute("book", booksService.findById(id));
        return "books/edit";
    }

    @PatchMapping("/{id}")
    public String update(@ModelAttribute("book") @Valid Book book, BindingResult bindingResult,
                         @PathVariable("id") int id) {

        if(bindingResult.hasErrors())
            return "books/edit";

        booksService.update(id, book);
        return "redirect:/books";
    }

    @PatchMapping("/{id}/add")
    public String giveBookSomeone(@ModelAttribute("person") Person person, @PathVariable("id") int bookId) {
        booksService.giveOrCancelBook(bookId, person);
        return "redirect:/books";
    }

    @PatchMapping("/{id}/cancel")
    public String cancelBook(@PathVariable("id") int bookId) {
        booksService.giveOrCancelBook(bookId, null);
        return "redirect:/books/{id}";
    }

    @DeleteMapping("/{id}")
    public String deleteBook(@PathVariable("id") int bookId) {
        booksService.delete(bookId);
        return "redirect:/books";
    }

    @GetMapping("/search")
    public String searchBook(@ModelAttribute("book") Book book, @RequestParam(value = "title", required = false) String title, Model model){

            List<Book> books = booksService.findBooksByTitleStartingWith(title);

            model.addAttribute("books", books);
            model.addAttribute("title", title);

        return "books/search";
    }

}
