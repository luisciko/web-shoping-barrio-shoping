package com.edu.proyecto.controller;

import java.net.MalformedURLException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.servletapi.SecurityContextHolderAwareRequestWrapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.edu.proyecto.models.entity.Comercio;
import com.edu.proyecto.models.entity.Producto;
import com.edu.proyecto.models.entity.UnidadMedida;
import com.edu.proyecto.models.service.IComercioService;
import com.edu.proyecto.models.service.IProductoServices;
import com.edu.proyecto.models.service.IUploadFileService;

@Controller
@SessionAttributes("comercio")
@RequestMapping("/")
public class ComercioControler {
	private final Logger log = LoggerFactory.getLogger(this.getClass());
	@Autowired
	private MessageSource messageSource;

	@Autowired
	private IComercioService comercioService;
	@Autowired
	private IProductoServices productoService;

	@Autowired
	private IUploadFileService uploadFileService;
	
	@GetMapping("/")
	public String listar(Model mode, Authentication authentication, HttpServletRequest request) {
		List<Comercio> comercios = comercioService.findAll();
		if (authentication != null) {
			log.info("Hola usuario autenticado, tu username es: ".concat(authentication.getName()));
		} else {
			log.info("Su usuario no esta autenticado");
		}
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if(request.isUserInRole("ROLE_ADMIN")) {
			log.info("Forma usando HttpServletRequest: Hola ".concat(auth.getName()).concat(" tienes acceso!"));
		} else {
			log.info("Forma usando HttpServletRequest: Hola ".concat(auth.getName()).concat(" NO tienes acceso!"));
		}
		mode.addAttribute("comercios", comercios);
		return "comercio/listado-comercios";
//		return "index";
	}

	@GetMapping("/starter")
	public String starter(Model model) {
		return "starter";
	}

	private boolean hasRole(String role) {

		SecurityContext context = SecurityContextHolder.getContext();

		if (context == null) {
			return false;
		}

		Authentication auth = context.getAuthentication();

		if (auth == null) {
			return false;
		}

		Collection<? extends GrantedAuthority> authorities = auth.getAuthorities();

		return authorities.contains(new SimpleGrantedAuthority(role));

	}
	@GetMapping(value = "/uploads/{filename:.+}")
	public ResponseEntity<Resource> verFoto(@PathVariable(value = "filename") String filename) {

		Resource recurso = null;

		try {
			recurso = uploadFileService.load(filename,null);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}

		return ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + recurso.getFilename() + "\"")
				.body(recurso);
	}
}
