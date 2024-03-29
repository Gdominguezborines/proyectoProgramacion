package com.proyecto.app.controller;

import com.proyecto.app.entity.Clientes;
import com.proyecto.app.Excepciones.ExcepcionNoEncontrado;
import com.proyecto.app.service.ClienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

/**
 * Creado por @autor: angel
 * El  16 de mar. de 2021.
 * //-encoding utf8 -docencoding utf8 -charset utf8(Para el javadoc)
 **/


@RestController
// Combinación entre Controller y ResponseBody (Controller para controlador; ResponseBody indicamos que el valor de retorno de nuestros métodos tiene que ser cuerpo solicitud (JSON))
@RequestMapping("/api/clientes") //para acceder a nuestros recursos de usuario a través de la url con navegador
public class ClientesController {

    //El controlador se va a comunicar con los métodos creados en la interface del service a través de inyección de dependencias
    @Autowired
    private ClienteService clienteService;


    //Creamos un nuevo cliente

    @PostMapping  // Recurso Post para crear del api REST
    public ResponseEntity<?> crearCliente (@RequestBody Clientes cliente){ // Con la @RequestBody le decimos que recibimos en el cuerpo un cliente
        // Método recibe en el cuerpo de la petición un cliente
        return ResponseEntity.status(HttpStatus.CREATED).body(clienteService.save(cliente)); //guardamos el cliente y lo devolvemos(save) y devolvemos un código 201 (creado ok con httpStatus)
    }

    //Leer un cliente

    @GetMapping("/{dni}") //Operación Get; mandamos un parámetro variable ({}) para decirle que cliente queremos que nos devuelva
    public ResponseEntity<?> leerCliente (@PathVariable(value = "dni") String clienteNif) throws ExcepcionNoEncontrado { //con el value decimos que el parámetro del método es =dni (parámetro del GetMapping)
        Optional<Clientes> oClientes = clienteService.findById(clienteNif); // llamamos a nuestro método del service y le pasamos un dni del cliente

        if (!oClientes.isPresent()){ //si no está presente el usuario
            throw new ExcepcionNoEncontrado(ResponseEntity.notFound().build()); // Si no encuentra al cliente mandamos una excepción con un ResponseEntity de no encontrado(error 404)

        }
        return ResponseEntity.ok(oClientes); // devolvemos un código de ok (200) y devolvemos el cliente
    }

    // Actualizar un cliente

    @PutMapping ("/{dni}") // Operación de tipo PUT porque vamos a cambiar datos del objeto
    public ResponseEntity<?> actualizarCliente(@RequestBody Clientes opcionesCliente, @PathVariable(value = "dni") String dniCliente) throws ExcepcionNoEncontrado{
        Optional<Clientes> oCliente = clienteService.findById(dniCliente); // En los optional no existe null, por eso vamos a tratarlo con nuestra excepcion
        if (!oCliente.isPresent()){
            throw new ExcepcionNoEncontrado(ResponseEntity.notFound().build());
        }
        // Devolvemos cada parámetro de nuestro Optional (get) y le asignamos (set) el valor  del cliente que le pasemos por parámetro
        oCliente.get().setNum_cliente(opcionesCliente.getNum_cliente());
        oCliente.get().setApellido(opcionesCliente.getApellido());
        oCliente.get().setNombre(opcionesCliente.getNombre());
        oCliente.get().setDireccion(opcionesCliente.getDireccion());
        // Devolvemos un código 201 (creado ok) y el usuario con el método save
        return ResponseEntity.status(HttpStatus.CREATED).body(clienteService.save(oCliente.get())); // uso el ocliente.get() porque oCliente es un Optional y el método save devuelve un Cliente


    }

    // Borrar un cliente
    @DeleteMapping ("/{dni}") //Operación de tipo delete
    public ResponseEntity<?> borrarUsuario (@PathVariable(value = "dni") String dniCliente) throws ExcepcionNoEncontrado{
        if (!clienteService.findById(dniCliente).isPresent()){
            throw new ExcepcionNoEncontrado(ResponseEntity.notFound().build());

        }
        clienteService.deleteById(dniCliente); // borramos el cliente que corresponda el dni por parámetro de método
        return ResponseEntity.ok().build(); // Devolvemos una respuesta http ok (200)
    }


}
