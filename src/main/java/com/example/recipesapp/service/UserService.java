package com.example.recipesapp.service;

import com.example.recipesapp.model.User;
import com.example.recipesapp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service //Clase como un servicio de Spring
public class UserService {
	
	@Autowired
	private UserRepository userRepository;
	
	//Crear o actualizar un usuario
	public User saveUser(User username) {
		return userRepository.save(username);
	}
	
	//Listar todos los usuarios
	public List<User> getAllUsers(){
		return userRepository.findAll();
	}
	
	//Buscar usuario por ID
	public Optional<User> getUserById(Long id){
		return userRepository.findById(id);
	}
	
	//Eliminar usuario
	public void deleteUser(Long id) {
		userRepository.deleteById(id);
	}
}
