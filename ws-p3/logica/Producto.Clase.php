<?php

require_once '../datos/Conexion.clase.php';

class Producto extends Conexion {

    private $codigoProducto;
    private $nombre;
    private $precioVenta;

    function getCodigoProducto() {
        return $this->codigoProducto;
    }

    function getNombre() {
        return $this->nombre;
    }

    function getPrecioVenta() {
        return $this->precioVenta;
    }

    function setCodigoProducto($codigoProducto) {
        $this->codigoProducto = $codigoProducto;
    }

    function setNombre($nombre) {
        $this->nombre = $nombre;
    }

    function setPrecioVenta($precioVenta) {
        $this->precioVenta = $precioVenta;
    }

    public function listar() {
        try {
            $sql = "select * from producto order by 2";
            $sentencia = $this->dblink->prepare($sql);
            $sentencia->execute();
            $resultado = $sentencia->fetchAll(PDO::FETCH_ASSOC);
            return $resultado;
        } catch (Exception $exc) {
            throw $exc;
        }
    }

    public function obtenerFoto($id) {
        $foto = "../imagenes-producto/" . $id;

        if (file_exists($foto . ".png")) {
            $foto = $foto . ".png";
        } else if (file_exists($foto . ".PNG")) {
            $foto = $foto . ".PNG";
        } else if (file_exists($foto . ".jpg")) {
            $foto = $foto . ".jpg";
        } else if (file_exists($foto . ".JPG")) {
            $foto = $foto . ".JPG";
        } else {
            $foto = "none";
        }

        if ($foto == "none") {
            return $foto;
        } else {
            return Funciones::$DIRECCION_WEB_SERVICE . $foto;
        }
    }

}
