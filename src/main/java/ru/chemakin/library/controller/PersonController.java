package ru.chemakin.library.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.chemakin.library.model.Book;
import ru.chemakin.library.model.Person;
import ru.chemakin.library.servises.PersonService;

import javax.validation.Valid;
import java.util.List;

@Controller
@RequestMapping("/people")
public class PersonController {

    private final PersonService personService;

    @Autowired
    public PersonController(PersonService personService) {
        this.personService = personService;
    }

    /**
     * Возвращает список всех людей в базе данных, добавляет список людей в модель
     * и возвращает представление "people/index".
     */
    @GetMapping
    public String index(Model model){
        model.addAttribute("person", personService.findAll());
        return "people/index";
    }

    /**
     * Принимает ID человека в базе данных и возвращает информацию о нем,
     * добавляет информацию о человеке и книгах, которые находятся у него на руках в модель
     * и возвращает представление "people/show".
     */
    @GetMapping("/{id}")
    public String show(@PathVariable("id") int id, Model model){
        List<Book> books = personService.getAllPersonBooks(id);
        model.addAttribute("person", personService.findOne(id));
        model.addAttribute("books", books);
        model.addAttribute("condition", !books.isEmpty()); // добавить person_id в модель
        return "people/show";
    }

    /**
     * Принимает ID человека в базе данных и удаляет его из базы данных.
     * Затем он перенаправляет пользователя на представление "redirect:/people".
     */
    @DeleteMapping("/{id}")
    public String delete(@PathVariable("id") int id){
        personService.delete(id);
        return "redirect:/people";
    }

    /**
     * Принимает ID человека в базе данных и возвращает представление "people/edit",
     * которое позволяет пользователю отредактировать информацию о человеке.
     */
    @GetMapping("/{id}/edit")
    public String edit(Model model, @PathVariable("id") int id) {
        model.addAttribute("person", personService.findOne(id));
        return "people/edit";
    }

    /**
     * Принимает id и вызывает метод для обновления данных человека в БД.
     * Если в процессе валидации данных возникают ошибки, метод возвращает представление "people/edit",
     * иначе он обновляет информацию о человеке в базе данных с помощью метода update() объекта personDAO и
     * перенаправляет пользователя на представление "redirect:/people".
     */
    @PatchMapping("/{id}")
    public String update(@ModelAttribute("person") @Valid Person person,
                         BindingResult bindingResult,
                         @PathVariable("id") int id) {
        if (bindingResult.hasErrors()) {
            return "people/edit";
        }
        personService.update(id, person);
        return "redirect:/people";
    }


    /**
     * Метод добавляет в модель объект Person с именем "person" и возвращает представление "people/new".
     * @param person Объект Person, который будет добавлен в модель представления.
     * @return Имя представления, которое будет отображено на странице.
     **/
    @GetMapping("/new")
    public String newPerson(@ModelAttribute("person") Person person){
        return "people/new";
    }

    /**
     * Метод вызывается после того, как пользователь заполняет и отправляет форму.
     * Метод получает объект Person из модели, выполняет валидацию и сохраняет его в базе данных, если ошибок не найдено.
     * @param person Объект Person, полученный из модели представления, который будет сохранен в базе данных.
     * @param bindingResult Результат связывания данных, который содержит информацию об ошибках валидации, если они есть.
     * @return Имя представления, на которое будет выполнен редирект после успешного сохранения в базе данных.
     **/
    @PostMapping()
    public String create(@ModelAttribute("person") @Valid Person person,
                         BindingResult bindingResult){
        if(bindingResult.hasErrors()){
            // Если валидация не прошла успешно и были найдены ошибки, вернуть имя представления "people/new" для отображения ошибок на форме.
            return "people/new";
        }

        // Если валидация прошла успешно, сохранить объект Person в базе данных и выполнить редирект на страницу со списком людей.
        personService.save(person);
        return "redirect:/people";
    }
}
