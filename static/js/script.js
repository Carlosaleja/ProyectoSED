// Manejar el envío del formulario de registro
document.getElementById('registroForm')?.addEventListener('submit', async (e) => {
    e.preventDefault();
	console.log("Intentando registrar usuario...");
    const nombre = document.getElementById('nombre').value;
    const correo = document.getElementById('correo').value;
    const contraseña = document.getElementById('contraseña').value;
    const confirmarContraseña = document.getElementById('confirmarContraseña').value;
    const carrera = document.getElementById('carrera').value;
    const edad = document.getElementById('edad').value;

    if (contraseña !== confirmarContraseña) {
        alert("Las contraseñas no coinciden.");
        return;
    }

    try {
        const response = await fetch('http://192.168.1.5:8080/api/usuarios/registro', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ nombre, correo, contraseña, carrera, edad })
        });

        if (response.ok) {
            alert("Usuario registrado con éxito");
            window.location.href = "/login.html";
        } else {
            const errorData = await response.json();
            alert(errorData.message || "Error en el registro");
        }
    } catch (error) {
        console.error("Error en el registro:", error);
        alert("Ocurrió un error en el registro. Inténtalo nuevamente.");
    }
});


// Manejar el envío del formulario de inicio de sesión

document.getElementById('loginForm')?.addEventListener('submit', async (e) => {
    e.preventDefault();
    const correo = document.getElementById('correo').value;
    const contraseña = document.getElementById('contraseña').value;

    try {
        const response = await fetch('http://192.168.1.5:8080/api/usuarios/login', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ correo, contraseña })
        });

        const data = await response.json();
        if (data.success) {
            
	    sessionStorage.setItem('id', data.id);
            sessionStorage.setItem('usuarioId', data.usuarioId);
	    console.log("ID guardado en sessionStorage:", sessionStorage.getItem('id')); 
            sessionStorage.setItem('nombre', data.nombre);
            sessionStorage.setItem('correo', data.correo);
            sessionStorage.setItem('carrera', data.carrera);
            sessionStorage.setItem('edad', data.edad);

            window.location.href = "/perfil.html"; 
        } else {
            alert("Credenciales incorrectas.");
        }
    } catch (error) {
        console.error("Error en el inicio de sesión:", error);
        alert("Ocurrió un error al iniciar sesión. Inténtalo nuevamente.");
    }
});


// Cargar datos del usuario en la página de perfil
document.addEventListener('DOMContentLoaded', () => {
    if (window.location.pathname === "/perfil.html") {
        document.getElementById('nombreUsuario').innerText = sessionStorage.getItem('nombre') || "Invitado";
        document.getElementById('correoUsuario').innerText = sessionStorage.getItem('correo');
        document.getElementById('carreraUsuario').innerText = sessionStorage.getItem('carrera') || "No especificado";
        document.getElementById('edadUsuario').innerText = sessionStorage.getItem('edad') || "No especificado";
    }
});

// Función para cerrar sesión
function cerrarSesion() {
    sessionStorage.clear(); 
    window.location.href = "/index.html"; 
}

/*
document.getElementById('crearEventoForm')?.addEventListener('submit', async (e) => {
    e.preventDefault();

    const usuarioId = sessionStorage.getItem('usuarioId');
    if (!usuarioId) {
        alert('Error: No se encontró el usuario autenticado.');
        return;
    }

    const evento = {
        titulo: document.getElementById('titulo').value.trim(),
        descripcion: document.getElementById('descripcion').value.trim(),
        categoria: document.getElementById('categoria').value.trim() || 'General',
        fecha: document.getElementById('fecha').value.trim(),
        importancia: document.getElementById('importancia').value.trim(),
        usuarioId: parseInt(usuarioId) // Asegúrate de enviar el ID como número
    };


	 console.log("Evento a enviar:", evento);

    // Verifica campos obligatorios
    if (!evento.titulo || !evento.descripcion || !evento.fecha || !evento.importancia) {
        alert('Por favor, completa todos los campos obligatorios.');
        return;
    }

    try {
        const response = await fetch('http://192.168.1.5:8080/api/eventos', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(evento)
        });

        if (response.ok) {
            alert('Evento publicado con éxito');
            document.getElementById('crearEventoForm').reset(); // Limpia el formulario
            cargarEventos(); // Recarga los eventos para incluir el nuevo
        } else {
            const error = await response.json();
            alert(`Error al publicar el evento: ${error.message || 'Error desconocido'}`);
        }
    } catch (error) {
        console.error('Error en la solicitud:', error);
        alert('Ocurrió un error al publicar el evento. Por favor, inténtalo nuevamente.');
    }
});
*/

/*
document.getElementById('crearEventoForm')?.addEventListener('submit', async (e) => {
    e.preventDefault();

    // Obtén el usuarioId desde sessionStorage
    const id = sessionStorage.getItem('id');
    if (!id) {
        alert('Error: No se encontró el usuario autenticado.');
        return;
    }

    // Crea el objeto evento con el usuarioId incluido
    const evento = {
        titulo: document.getElementById('titulo').value.trim(),
        descripcion: document.getElementById('descripcion').value.trim(),
        categoria: document.getElementById('categoria').value.trim() || 'General',
        fecha: document.getElementById('fecha').value.trim(),
        importancia: document.getElementById('importancia').value.trim(),
        usuarioId: parseInt(id) // Asegúrate de enviar el usuarioId como número
    };

    console.log("Evento a enviar:", evento); // Verificación de los datos del evento antes de enviarlos

    try {
        // Envía la solicitud POST al backend con el evento
        const response = await fetch('http://192.168.1.5:8080/api/eventos', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(evento)
        });

        if (response.ok) {
            alert('Evento publicado con éxito');
            document.getElementById('crearEventoForm').reset(); // Limpia el formulario
            cargarEventos(); // Recarga los eventos para incluir el nuevo
        } else {
            const error = await response.json();
            alert(`Error al publicar el evento: ${error.message || 'Error desconocido'}`);
        }
    } catch (error) {
        console.error('Error en la solicitud:', error);
        alert('Ocurrió un error al publicar el evento. Por favor, inténtalo nuevamente.');
    }
}); */

document.getElementById('crearEventoForm')?.addEventListener('submit', async (e) => {
    e.preventDefault();

    // Obtén el usuarioId desde sessionStorage
    const id = sessionStorage.getItem('id');
    if (!id) {
        alert('Error: No se encontró el usuario autenticado.');
        console.error("ID de usuario no encontrado en sessionStorage.");
        return;
    }

    const evento = {
        titulo: document.getElementById('titulo').value.trim(),
        descripcion: document.getElementById('descripcion').value.trim(),
        categoria: document.getElementById('categoria').value.trim() || 'General',
        fecha: document.getElementById('fecha').value.trim(),
        importancia: document.getElementById('importancia').value.trim(),
        usuarioId: parseInt(id)
    };

    try {
        const response = await fetch('http://192.168.1.5:8080/api/eventos', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(evento)
        });



        if (response.ok) {
            alert('Evento publicado con éxito');
            document.getElementById('crearEventoForm').reset();
            cargarEventos(); 
        } else {
            const error = await response.json();
            alert(`Error al publicar el evento: ${error.message || 'Error desconocido'}`);
            console.error("Error en la respuesta del servidor:", error);
        }
    } catch (error) {
        console.error('Error en la solicitud:', error);
        alert('Ocurrió un error al publicar el evento. Por favor, inténtalo nuevamente.');
    }
});


async function cargarEventos() {
    try {
        const response = await fetch('http://192.168.1.5:8080/api/eventos');

        
        if (!response.ok) {
            throw new Error(`Error al obtener eventos: ${response.status}`);
        }
        const eventos = await response.json();

        const eventosContainer = document.getElementById('eventosContainer');
        eventosContainer.innerHTML = ''; 

        // Crea las cards dinámicamente
        eventos.forEach(evento => {
            const card = document.createElement('div');
            card.className = 'card';
            card.innerHTML = `
                <h4>${evento.titulo}</h4>
                <p>${evento.descripcion}</p>
                <p>Categoría: ${evento.categoria || 'No especificada'}</p>
                <p>Fecha: ${evento.fecha}</p>
                <p>Importancia: ${evento.importancia}</p>
            `;
            eventosContainer.appendChild(card);
        });
    } catch (error) {
        console.error('Error al cargar eventos:', error);
    }
}

document.addEventListener('DOMContentLoaded', cargarEventos);

