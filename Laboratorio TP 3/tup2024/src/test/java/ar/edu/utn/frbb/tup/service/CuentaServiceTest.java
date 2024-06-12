package ar.edu.utn.frbb.tup.service;

import ar.edu.utn.frbb.tup.model.Cliente;
import ar.edu.utn.frbb.tup.model.Cuenta;
import ar.edu.utn.frbb.tup.model.TipoCuenta;
import ar.edu.utn.frbb.tup.model.exception.ClienteAlreadyExistsException;
import ar.edu.utn.frbb.tup.model.exception.CuentaAlreadyExistsException;
import ar.edu.utn.frbb.tup.model.exception.TipoCuentaAlreadyExistsException;
import ar.edu.utn.frbb.tup.model.exception.CuentaNoSoportadaException;
import ar.edu.utn.frbb.tup.persistence.CuentaDao;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CuentaServiceTest {

    @Mock
    private CuentaDao cuentaDao;

    @Mock
    private ClienteService clienteService;

    @InjectMocks
    private CuentaService cuentaService;

    @BeforeEach
    public void setUp() {
        // No se requiere MockitoAnnotations.openMocks(this); con MockitoExtension.class
    }

    @Test
    public void testCuentaExistente() throws CuentaAlreadyExistsException, TipoCuentaAlreadyExistsException, CuentaNoSoportadaException {
        Cuenta cuentaExistente = new Cuenta();
        cuentaExistente.setNumeroCuenta(12345);

        when(cuentaDao.find(12345)).thenReturn(cuentaExistente);

        assertThrows(CuentaAlreadyExistsException.class, () -> cuentaService.darDeAltaCuenta(cuentaExistente, 12345678));

        verify(cuentaDao, times(1)).find(12345);
    }

    @Test
    public void testCuentaNoSoportada() throws TipoCuentaAlreadyExistsException, CuentaNoSoportadaException {
        Cuenta cuentaNoSoportada = new Cuenta();
        cuentaNoSoportada.setTipoCuenta(TipoCuenta.CUENTA_NO_SOPORTADA);

        Cliente cliente = new Cliente();
        cliente.setDni(12345678);

        when(clienteService.buscarClientePorDni(12345678)).thenReturn(cliente);

        assertThrows(CuentaNoSoportadaException.class,
                () -> cuentaService.darDeAltaCuenta(cuentaNoSoportada, 12345678));
    }

    @Test
    public void testClienteYaTieneCuentaDeEseTipo() throws TipoCuentaAlreadyExistsException, CuentaNoSoportadaException {
        Cliente cliente = new Cliente();
        cliente.setDni(12345678);
        Cuenta cuentaExistente = new Cuenta();
        cuentaExistente.setTipoCuenta(TipoCuenta.CAJA_AHORRO);
        cliente.addCuenta(cuentaExistente);

        Cuenta cuentaNueva = new Cuenta();
        cuentaNueva.setTipoCuenta(TipoCuenta.CAJA_AHORRO);

        when(clienteService.buscarClientePorDni(12345678)).thenReturn(cliente);

        assertThrows(TipoCuentaAlreadyExistsException.class, () -> cuentaService.darDeAltaCuenta(cuentaNueva, 12345678));

        verify(clienteService, times(1)).buscarClientePorDni(12345678);
    }

    @Test
    public void testCuentaCreadaExitosamente() throws Exception, ClienteAlreadyExistsException, TipoCuentaAlreadyExistsException, CuentaNoSoportadaException {
        Cuenta cuentaNueva = new Cuenta();
        cuentaNueva.setNumeroCuenta(12345);
        cuentaNueva.setTipoCuenta(TipoCuenta.CAJA_AHORRO);

        when(cuentaDao.find(12345)).thenReturn(null);

        Cliente cliente = new Cliente();
        cliente.setDni(12345678);
        when(clienteService.buscarClientePorDni(12345678)).thenReturn(cliente);

        assertDoesNotThrow(() -> cuentaService.darDeAltaCuenta(cuentaNueva, 12345678));

        verify(cuentaDao, times(1)).save(cuentaNueva);
    }

}




