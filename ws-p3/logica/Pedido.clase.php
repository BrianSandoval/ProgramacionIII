<?php

require_once '../datos/Conexion.clase.php';

class Pedido extends Conexion {

    public function registrarPedido($p_dni, $p_det_ped) {
        try {
            $sql = "
                    select f_registrar_pedido
                    (
                            :dni_cli,
                            :det_ped
                    ) as np;
                ";

            $sentencia = $this->dblink->prepare($sql);
            $sentencia->bindParam(":dni_cli", $p_dni);
            $sentencia->bindParam(":det_ped", $p_det_ped);
            $sentencia->execute();
            $resultado = $sentencia->fetch(PDO::FETCH_ASSOC);
            return $resultado;
        } catch (Exception $exc) {
            throw $exc;
        }
    }

}
