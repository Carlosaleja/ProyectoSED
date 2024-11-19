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
	    sessionStorage.setItem('rol', data.rol);

		console.log("Rol:", sessionStorage.getItem('rol'));


            window.location.href = "/perfil.html"; 
        } else {
            alert("Credenciales incorrectas.");
        }
    } catch (error) {
        console.error("Error en el inicio de sesión:", error);
        alert("Ocurrió un error al iniciar sesión. Inténtalo nuevamente.");
    }
});


// Cargar datos del usuario 
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

document.getElementById('crearEventoForm')?.addEventListener('submit', async (e) => {
    e.preventDefault();

    
 	const id = sessionStorage.getItem('id');
    const usuarioId = sessionStorage.getItem('id'); 

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
            headers: { 'Content-Type': 'application/json',
			'Usuario-ID': sessionStorage.getItem('id'),
        		'Rol': sessionStorage.getItem('rol'), 
	},
            body: JSON.stringify(evento)
        });



        if (response.ok) {
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
//-----------------------------------------------------------------
/*
async function cargarEventos() {
    try {
        const response = await fetch('http://192.168.1.5:8080/api/eventos');

        if (!response.ok) {
            throw new Error(`Error al obtener eventos: ${response.status}`);
        }
        const eventos = await response.json();

        const eventosContainer = document.getElementById('eventosContainer');
        eventosContainer.innerHTML = ''; 

        const usuarioId = sessionStorage.getItem('id'); 
        const esAdmin = sessionStorage.getItem('rol') === 'admin';
	const esSuperAdmin = sessionStorage.getItem('rol') === 'super_admin'; 

        eventos.forEach(evento => {
            const card = document.createElement('div');
            card.className = 'card';
            card.innerHTML = `
                <h4>${evento.titulo}</h4>
                <p>${evento.descripcion}</p>
                <p><strong>Categoría:</strong> ${evento.categoria || 'No especificada'}</p>
                <p><strong>Fecha:</strong> ${evento.fecha}</p>
                <p><strong>Importancia:</strong> ${evento.importancia}</p>
            `;

            if (evento.usuarioId == usuarioId || esAdmin || esSuperAdmin) {
                const actionsDiv = document.createElement('div');
                actionsDiv.className = 'actions';

                const editButton = document.createElement('button');
                editButton.className = 'edit-btn';
                editButton.textContent = 'Editar';
                editButton.dataset.eventId = evento.id; 
                editButton.addEventListener('click', () => mostrarFormularioEdicion(evento));
                actionsDiv.appendChild(editButton);

                const deleteButton = document.createElement('button');
                deleteButton.className = 'delete-btn';
                deleteButton.textContent = 'Eliminar';
                deleteButton.dataset.eventId = evento.id; 
                deleteButton.addEventListener('click', () => eliminarEvento(evento.id));
                actionsDiv.appendChild(deleteButton);

                card.appendChild(actionsDiv);
            }

            eventosContainer.appendChild(card);
        });
    } catch (error) {
        console.error('Error al cargar eventos:', error);
    }
}*/

async function cargarEventos() {
    try {
        const response = await fetch('http://192.168.1.5:8080/api/eventos');

        if (!response.ok) {
            throw new Error(`Error al obtener eventos: ${response.status}`);
        }

        const eventos = await response.json();
        const eventosContainer = document.getElementById('eventosContainer');

        if (!eventosContainer) {
            console.error("No se encontró el contenedor de eventos.");
            return;
        }

        eventosContainer.innerHTML = ''; 

        const usuarioId = sessionStorage.getItem('id');
        const esAdmin = sessionStorage.getItem('rol') === 'admin';
        const esSuperAdmin = sessionStorage.getItem('rol') === 'super_admin';

        eventos.forEach(evento => {
            console.log(`Evento ID: ${evento.id}, Usuario ID del evento: ${evento.usuarioId}`);

            const card = document.createElement('div');
            card.className = 'card';
            card.innerHTML = `
                <h4>${evento.titulo}</h4>
                <p>${evento.descripcion}</p>
                <p><strong>Categoría:</strong> ${evento.categoria || 'No especificada'}</p>
                <p><strong>Fecha:</strong> ${evento.fecha}</p>
                <p><strong>Importancia:</strong> ${evento.importancia}</p>
            `;

            if (evento.usuarioId == usuarioId || esAdmin || esSuperAdmin) {
                console.log(`Permisos otorgados para evento: ${evento.id}`);

                const actionsDiv = document.createElement('div');
                actionsDiv.className = 'actions';

                const editButton = document.createElement('button');
                editButton.className = 'edit-btn';
                editButton.textContent = 'Editar';
                editButton.dataset.eventId = evento.id; 
                editButton.addEventListener('click', () => {
                    console.log("Editar evento:", evento);
                    mostrarFormularioEdicion(evento);
                });
                actionsDiv.appendChild(editButton);

                const deleteButton = document.createElement('button');
                deleteButton.className = 'delete-btn';
                deleteButton.textContent = 'Eliminar';
                deleteButton.dataset.eventId = evento.id; 
                deleteButton.addEventListener('click', () => {
                    console.log("Eliminar evento:", evento.id);
                    eliminarEvento(evento.id);
                });
                actionsDiv.appendChild(deleteButton);

                card.appendChild(actionsDiv);
            } else {
                console.log(`Sin permisos para evento: ${evento.id}`);
		console.log('ID Usuario:', sessionStorage.getItem('id'));
		console.log('Rol:', sessionStorage.getItem('rol'));

            }

            eventosContainer.appendChild(card);
        });
    } catch (error) {
        console.error('Error al cargar eventos:', error);
    }
}


// edicion

function mostrarFormularioEdicion(evento) {
    document.getElementById('editarEventoPanel').classList.remove('hidden');
    document.getElementById('eventoId').value = evento.id;
    document.getElementById('editarTitulo').value = evento.titulo;
    document.getElementById('editarDescripcion').value = evento.descripcion;
    document.getElementById('editarCategoria').value = evento.categoria || '';
    document.getElementById('editarFecha').value = evento.fecha;
    document.getElementById('editarImportancia').value = evento.importancia;
}
/*
document.getElementById('editarEventoForm').addEventListener('submit', async (e) => {
    e.preventDefault();

    const eventoId = document.getElementById('eventoId').value.trim();
    const usuarioId = sessionStorage.getItem('id');
    
    const eventoActualizado = {
        id: parseInt(eventoId),
        titulo: document.getElementById('editarTitulo').value.trim(),
        descripcion: document.getElementById('editarDescripcion').value.trim(),
        categoria: document.getElementById('editarCategoria').value.trim(),
        fecha: document.getElementById('editarFecha').value.trim(),
        importancia: document.getElementById('editarImportancia').value.trim(),
	usuarioId: parseInt(usuarioId)
    };

    if (!eventoActualizado.id || !eventoActualizado.titulo || !eventoActualizado.descripcion || !eventoActualizado.fecha || !eventoActualizado.importancia) {
        alert('Por favor, completa todos los campos obligatorios.');
        return;
    }

    try {
      
        const response = await fetch('http://192.168.1.5:8080/api/eventos', {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json',
                'Usuario-ID': sessionStorage.getItem('id'),
		'Rol': sessionStorage.getItem('rol'),
            },
            body: JSON.stringify(eventoActualizado),
        });

        if (response.ok) {
            alert('Evento actualizado con éxito');
            document.getElementById('editarEventoPanel').classList.add('hidden');
            cargarEventos();
        } else if (response.status === 403) {
            alert('No tienes permisos para editar este evento.');
        } else {
            alert('Error al actualizar el evento.');
        }
    } catch (error) {
        console.error('Error al actualizar el evento:', error);
        alert('Ocurrió un error al intentar actualizar el evento.');
    }
});*/

document.addEventListener('DOMContentLoaded', () => {
    // Verificar y cargar eventos
    const eventosContainer = document.getElementById('eventosContainer');
    if (eventosContainer) {
        cargarEventos();
    } else {
        console.error('No se encontró el contenedor de eventos.');
    }

    // Verificar si el formulario de edición existe en el DOM
    const editarEventoForm = document.getElementById('editarEventoForm');
    if (editarEventoForm) {
        editarEventoForm.addEventListener('submit', async (e) => {
            e.preventDefault();

            const eventoId = document.getElementById('eventoId').value.trim();
            const usuarioId = sessionStorage.getItem('id');
            
            const eventoActualizado = {
                id: parseInt(eventoId),
                titulo: document.getElementById('editarTitulo').value.trim(),
                descripcion: document.getElementById('editarDescripcion').value.trim(),
                categoria: document.getElementById('editarCategoria').value.trim(),
                fecha: document.getElementById('editarFecha').value.trim(),
                importancia: document.getElementById('editarImportancia').value.trim(),
                usuarioId: parseInt(usuarioId),
            };

            try {
                const response = await fetch('http://192.168.1.5:8080/api/eventos', {
                    method: 'PUT',
                    headers: {
                        'Content-Type': 'application/json',
                        'Usuario-ID': sessionStorage.getItem('id'),
                        'Rol': sessionStorage.getItem('rol'),
                    },
                    body: JSON.stringify(eventoActualizado),
                });

                if (response.ok) {
                    alert('Evento actualizado con éxito');
                    document.getElementById('editarEventoPanel').classList.add('hidden');
                    cargarEventos();
                } else if (response.status === 403) {
                    alert('No tienes permisos para editar este evento.');
                } else {
                    alert('Error al actualizar el evento.');
                }
            } catch (error) {
                console.error('Error al actualizar el evento:', error);
            }
        });
    } else {
        console.error('Formulario de edición de evento no encontrado.');
    }

    // Cancelar edición
    const cancelarEdicionBtn = document.getElementById('cancelarEdicion');
    if (cancelarEdicionBtn) {
        cancelarEdicionBtn.addEventListener('click', () => {
            document.getElementById('editarEventoPanel').classList.add('hidden');
        });
    }
});


/*
document.getElementById('editarEventoForm').addEventListener('submit', async (e) => {
    e.preventDefault();

    const eventoId = document.getElementById('eventoId').value.trim();
    const usuarioId = sessionStorage.getItem('id');
    const rol = sessionStorage.getItem('rol'); // Obtiene el rol del usuario

    const eventoActualizado = {
        id: parseInt(eventoId),
        titulo: document.getElementById('editarTitulo').value.trim(),
        descripcion: document.getElementById('editarDescripcion').value.trim(),
        categoria: document.getElementById('editarCategoria').value.trim(),
        fecha: document.getElementById('editarFecha').value.trim(),
        importancia: document.getElementById('editarImportancia').value.trim(),
        usuarioId: parseInt(usuarioId)
    };

    if (!eventoActualizado.id || !eventoActualizado.titulo || !eventoActualizado.descripcion || !eventoActualizado.fecha || !eventoActualizado.importancia) {
        alert('Por favor, completa todos los campos obligatorios.');
        return;
    }

    try {
        const response = await fetch('http://192.168.1.5:8080/api/eventos', {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json',
                'Usuario-ID': sessionStorage.getItem('id'), // Envía el ID del usuario
                'Usuario-Rol': sessionStorage.getItem('rol') // Envía el rol del usuario
            },
            body: JSON.stringify(eventoActualizado),
        });

        if (response.ok) {
            alert('Evento actualizado con éxito');
            document.getElementById('editarEventoPanel').classList.add('hidden'); // Oculta el formulario de edición
            cargarEventos(); // Recarga los eventos
        } else if (response.status === 403) {
            alert('No tienes permisos para editar este evento.');
        } else {
            alert('Error al actualizar el evento.');
        }
    } catch (error) {
        console.error('Error al actualizar el evento:', error);
        alert('Ocurrió un error al intentar actualizar el evento.');
    }
});
*/
/*
document.getElementById('cancelarEdicion').addEventListener('click', () => {
    document.getElementById('editarEventoPanel').classList.add('hidden');
});
*/
//eliminar un evento
async function eliminarEvento(eventId) {
    if (!confirm('¿Estás seguro de que deseas eliminar este evento?')) {
        return;
    }

    try {
        const response = await fetch('http://192.168.1.5:8080/api/eventos', {
            method: 'DELETE',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({
                id: eventId,
                usuarioId: sessionStorage.getItem('id'),
		'Rol': sessionStorage.getItem('rol'),
            })
        });

        if (response.ok) {
            alert('Evento eliminado con éxito');
            cargarEventos(); 
        } else {
            alert('Error al eliminar el evento');
        }
    } catch (error) {
        console.error('Error al eliminar el evento:', error);
    }
}

// Eliminar un evento
/*async function eliminarEvento(eventId) {
    if (!confirm('¿Estás seguro de que deseas eliminar este evento?')) {
        return;
    }

    try {
        const response = await fetch('http://192.168.1.5:8080/api/eventos', {
            method: 'DELETE',
            headers: {
                'Content-Type': 'application/json',
                'Usuario-ID': sessionStorage.getItem('id'), // Envía el ID del usuario
                'Usuario-Rol': sessionStorage.getItem('rol') // Envía el rol del usuario
            },
            body: JSON.stringify({ id: eventId })
        });

        if (response.ok) {
            alert('Evento eliminado con éxito');
            cargarEventos(); // Recarga los eventos después de la eliminación
        } else if (response.status === 403) {
            alert('No tienes permisos para eliminar este evento.');
        } else {
            alert('Error al eliminar el evento.');
        }
    } catch (error) {
        console.error('Error al eliminar el evento:', error);
        alert('Ocurrió un error al intentar eliminar el evento.');
    }
}
*/


// Cargar eventos al cargar la página
document.addEventListener('DOMContentLoaded', cargarEventos);

document.getElementById('buscarEventosForm').addEventListener('submit', async (e) => {
    e.preventDefault();

    const titulo = document.getElementById('buscarTitulo').value.trim();
    if (!titulo) {
        alert('Por favor, ingresa un título para buscar.');
        return;
    }

    try {
        const response = await fetch(`http://192.168.1.5:8080/api/eventos/buscar?titulo=${encodeURIComponent(titulo)}`);
        if (!response.ok) {
            throw new Error('Error al buscar eventos');
        }

        const eventos = await response.json();

        const eventosContainer = document.getElementById('eventosContainer');
        eventosContainer.innerHTML = ''; // Limpia el contenedor

        if (eventos.length === 0) {
            eventosContainer.innerHTML = '<p>No se encontró ningún evento con ese título.</p>';
        } else {
            eventos.forEach(evento => {
                const card = document.createElement('div');
                card.className = 'card';
                card.innerHTML = `
                    <h4>${evento.titulo}</h4>
                    <p>${evento.descripcion}</p>
                    <p><strong>Categoría:</strong> ${evento.categoria || 'No especificada'}</p>
                    <p><strong>Fecha:</strong> ${evento.fecha}</p>
                    <p><strong>Importancia:</strong> ${evento.importancia}</p>
                `;
                eventosContainer.appendChild(card);
            });
        }
    } catch (error) {
        console.error('Error al buscar eventos:', error);
        alert('Ocurrió un error al buscar los eventos.');
    }
});
