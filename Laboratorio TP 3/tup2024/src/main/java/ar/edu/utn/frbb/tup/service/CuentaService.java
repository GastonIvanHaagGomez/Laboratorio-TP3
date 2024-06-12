package ar.edu.utn.frbb.tup.service;

import ar.edu.utn.frbb.tup.model.Cliente;
import ar.edu.utn.frbb.tup.model.Cuenta;
import ar.edu.utn.frbb.tup.model.TipoCuenta;
import ar.edu.utn.frbb.tup.model.exception.CuentaAlreadyExistsException;
import ar.edu.utn.frbb.tup.model.exception.TipoCuentaAlreadyExistsException;
import ar.edu.utn.frbb.tup.model.exception.CuentaNoSoportadaException;
import ar.edu.utn.frbb.tup.persistence.CuentaDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CuentaService {

    CuentaDao cuentaDao;
    ClienteService clienteService;

    @Autowired
    public CuentaService(CuentaDao cuentaDao, ClienteService clienteService) {
        this.cuentaDao = cuentaDao;
        this.clienteService = clienteService;
    }

    public Cuenta darDeAltaCuenta(Cuenta cuenta, int dni) throws CuentaAlreadyExistsException, TipoCuentaAlreadyExistsException, CuentaNoSoportadaException {
        Cliente cliente = clienteService.buscarClientePorDni(dni);

        // Verificación de tipo de cuenta no soportada
        if (cuenta.getTipoCuenta() == TipoCuenta.CUENTA_NO_SOPORTADA) {
            throw new CuentaNoSoportadaException("Tipo de cuenta no soportado: " + cuenta.getTipoCuenta());
        }

        // Verificación de cuenta existente
        if (cuentaDao.find(cuenta.getNumeroCuenta()) != null) {
            throw new CuentaAlreadyExistsException("Cuenta ya existe con número: " + cuenta.getNumeroCuenta());
        }

        // Verificación de tipo de cuenta ya existente para el cliente
        for (Cuenta c : cliente.getCuentas()) {
            if (c.getTipoCuenta() == cuenta.getTipoCuenta()) {
                throw new TipoCuentaAlreadyExistsException("Cliente ya tiene cuenta de tipo: " + cuenta.getTipoCuenta());
            }
        }

        cuentaDao.save(cuenta);
        return cuenta;
    }

    public Cuenta find(long id) {
        return cuentaDao.find(id);
    }
    public boolean tipoDeCuentaSoportada(String tipoCuenta) {
        if (tipoCuenta == null) {
            return false;
        }
        switch (tipoCuenta) {
            case "CA$":
            case "CC$":
            case "CAU$S":
                return true;
            default:
                return false;
        }
    }
}
