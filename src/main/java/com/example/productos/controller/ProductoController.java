package com.example.productos.controller;

import com.example.productos.models.Producto;
import com.example.productos.repository.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/api/v1/productos")
public class ProductoController {
    @Autowired
    ProductoRepository productoRepository;

    Map<String, Object> respuesta = new HashMap<>();

    @GetMapping("")
    public ResponseEntity<Map<String, Object>> getProductos(@RequestParam(required = false, defaultValue = "0") int limite,
                                                            @RequestParam(required = false, defaultValue = "asc") String sort) {
        respuesta.clear();
        ArrayList<Producto> productos = new ArrayList<>();

        if (limite <= 0) {
            limite = productoRepository.findAll().size();
        }

        for (int i = 0; i < limite; i++) {
            productos.add(productoRepository.findAll().get(i));
        }

        if (sort.equals("desc")) {
            productos.sort((o1, o2) -> o2.getId() - o1.getId());
        }

        if (productos.isEmpty()) {
            respuesta.put("Error", "No hay ningún producto");
            respuesta.put("STATUS", HttpStatus.NOT_FOUND);
            return new ResponseEntity<>(respuesta, HttpStatus.NOT_FOUND);
        }

        respuesta.put("STATUS", HttpStatus.OK);
        respuesta.put("MENSAJE", "Productos encontrados");
        respuesta.put("DATA", productos);
        return new ResponseEntity<>(respuesta, HttpStatus.OK);

    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getProductoPorId(@PathVariable int id) {
        respuesta.clear();

        if (id <= 0) {
            respuesta.put("ERROR", "Id erroneo");
            respuesta.put("STATUS", HttpStatus.BAD_REQUEST);
            return new ResponseEntity<>(respuesta, HttpStatus.BAD_REQUEST);
        }

        Producto producto = productoRepository.findById(id);

        if (producto == null) {
            respuesta.put("Error", "No existe el producto con el id " + id);
            respuesta.put("STATUS", HttpStatus.NOT_FOUND);
            return new ResponseEntity<>(respuesta, HttpStatus.NOT_FOUND);
        }

        respuesta.put("STATUS", HttpStatus.OK);
        respuesta.put("MENSAJE", "Producto encontrado");
        respuesta.put("DATA", producto);
        return new ResponseEntity<>(respuesta, HttpStatus.OK);
    }

    /*@GetMapping("/nombre/{nombre}")
    public ResponseEntity<Map<String, Object>> getProductoPorNombre(@PathVariable String nombre){
        respuesta.clear();

        if (nombre.isBlank()) {
            respuesta.put("ERROR", "El nombre no puede estar vacío");
            respuesta.put("STATUS", HttpStatus.BAD_REQUEST);
            return new ResponseEntity<>(respuesta, HttpStatus.BAD_REQUEST);
        }

        ArrayList<Producto> productos = productoRepository.findByNombre(nombre);

        if (productos.isEmpty()) {
            respuesta.put("ERROR", "No se han encontrado productos con ese nombre");
            respuesta.put("STATUS", HttpStatus.NOT_FOUND);
            return new ResponseEntity<>(respuesta, HttpStatus.NOT_FOUND);
        }

        respuesta.put("STATUS", HttpStatus.OK);
        respuesta.put("MENSAJE", "Productos encontrados");
        respuesta.put("DATA", productos);
        return new ResponseEntity<>(respuesta, HttpStatus.OK);
    }*/


    @GetMapping("/precio")
    public ResponseEntity<Map<String, Object>> getProductosPorPrecio(@RequestParam float precio) {
        respuesta.clear();

        if (precio <= 0) {
            respuesta.put("ERROR", "El precio debe ser mayor a 0");
            respuesta.put("STATUS", HttpStatus.BAD_REQUEST);
            return new ResponseEntity<>(respuesta, HttpStatus.BAD_REQUEST);
        }

        ArrayList<Producto> productos = productoRepository.findByPrecio(precio);

        if (productos.isEmpty()) {
            respuesta.put("ERROR", "No se han encontrado productos con ese precio");
            respuesta.put("STATUS", HttpStatus.NOT_FOUND);
            return new ResponseEntity<>(respuesta, HttpStatus.NOT_FOUND);
        }

        respuesta.put("STATUS", HttpStatus.OK);
        respuesta.put("MENSAJE", "Productos encontrados");
        respuesta.put("DATA", productos);
        return new ResponseEntity<>(respuesta, HttpStatus.OK);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> eliminarProducto(@PathVariable int id) {
        respuesta.clear();

        if (id <= 0) {
            respuesta.put("ERROR", "Id erroneo");
            respuesta.put("STATUS", HttpStatus.BAD_REQUEST);
            return new ResponseEntity<>(respuesta, HttpStatus.BAD_REQUEST);
        }

        /*Producto producto = productoRepository.findById(id);

        if (producto == null) {
            respuesta.put("ERROR", "No existe el producto con el id " + id);
            respuesta.put("STATUS", HttpStatus.NOT_FOUND);
            return new ResponseEntity<>(respuesta, HttpStatus.NOT_FOUND);
        }

        productoRepository.delete(producto);

        respuesta.put("STATUS", HttpStatus.OK);
        respuesta.put("MENSAJE", "Producto eliminado");
        return new ResponseEntity<>(respuesta, HttpStatus.OK);*/

        if (!productoRepository.existsById(id)) {
            respuesta.put("ERROR", "No existe el producto con el id " + id);
            respuesta.put("STATUS", HttpStatus.NOT_FOUND);
            return new ResponseEntity<>(respuesta, HttpStatus.NOT_FOUND);
        }

        productoRepository.deleteById(id);
        respuesta.put("STATUS", HttpStatus.OK);
        respuesta.put("MENSAJE", "Producto eliminado");
        return new ResponseEntity<>(respuesta, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> actualizaProducto(@PathVariable int id, @RequestBody Producto producto) {
        respuesta.clear();

        if (id <= 0) {
            respuesta.put("ERROR", "Id erroneo");
            respuesta.put("STATUS", HttpStatus.BAD_REQUEST);
            return new ResponseEntity<>(respuesta, HttpStatus.BAD_REQUEST);
        }

        if (!productoRepository.existsById(id)) {
            respuesta.put("ERROR", "No existe el producto con el id " + id);
            respuesta.put("STATUS", HttpStatus.NOT_FOUND);
            return new ResponseEntity<>(respuesta, HttpStatus.NOT_FOUND);
        }

        if (producto.getNombre().isBlank() || producto.getNombre() == null || producto.getPrecio().compareTo(BigDecimal.ZERO) <= 0) {
            respuesta.put("ERROR", "El nombre y el precio son obligatorios");
            respuesta.put("STATUS", HttpStatus.BAD_REQUEST);
            return new ResponseEntity<>(respuesta, HttpStatus.BAD_REQUEST);
        }

        Producto productoTemp = productoRepository.findById(id);
        productoTemp.setNombre(producto.getNombre());
        productoTemp.setPrecio(producto.getPrecio());
        productoTemp.setFoto(producto.getFoto());

        productoRepository.save(productoTemp);
        respuesta.put("STATUS", HttpStatus.OK);
        respuesta.put("MENSAJE", "Producto actualizado");
        return new ResponseEntity<>(respuesta, HttpStatus.OK);
    }

    @PostMapping("/nuevo")
    public ResponseEntity<Map<String, Object>> crearProducto(@RequestBody Producto producto) {
        respuesta.clear();

        // Validar solo campos obligatorios
        if (producto.getNombre() == null || producto.getNombre().isBlank() ||
                producto.getPrecio() == null || producto.getPrecio().compareTo(BigDecimal.ZERO) <= 0) {

            respuesta.put("ERROR", "El nombre y el precio son obligatorios y mayores a 0");
            return new ResponseEntity<>(respuesta, HttpStatus.BAD_REQUEST);
        }

        // Guardar directamente (JPA generará el ID)
        Producto nuevoProducto = productoRepository.save(producto);

        respuesta.put("STATUS", HttpStatus.CREATED);
        respuesta.put("DATA", nuevoProducto);
        return new ResponseEntity<>(respuesta, HttpStatus.CREATED);
    }
}
