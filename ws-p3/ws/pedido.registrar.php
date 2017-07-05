<?php

require_once '../logica/Pedido.clase.php';
require_once '../util/funciones/Funciones.clase.php';
require_once 'token.validar.php';

if (!isset($_POST["token"])) {
    Funciones::imprimeJSON(500, "Debe especificar un token", "");
    exit();
}

$token = $_POST["token"];

try {
    if (validarToken($token)) {
        $dni = $_POST["dni_cli"];
        $det_ped = $_POST["det_ped"];

        $objPed = new Pedido();
        $resultado = $objPed->registrarPedido($dni, $det_ped);

        Funciones::imprimeJSON(200, "", $resultado);
    }
} catch (Exception $exc) {
    Funciones::imprimeJSON(500, $exc->getMessage(), "");
}


