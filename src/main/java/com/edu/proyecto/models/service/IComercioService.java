package com.edu.proyecto.models.service;

import java.util.List;

import com.edu.proyecto.models.entity.Comercio;

public interface IComercioService {
	public Comercio findById(Long id);
	public List<Comercio> findAll();
}
