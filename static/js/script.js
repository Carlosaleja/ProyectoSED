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
            window.location.href = "/login.html"; // Redirige al login después del registro
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
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ correo, contraseña })
        });

        const data = await response.json();
        if (data.success) {
            // Guardar los datos del usuario en sessionStorage
            sessionStorage.setItem('nombre', data.nombre);
            sessionStorage.setItem('correo', data.correo);
            sessionStorage.setItem('carrera', data.carrera);
            sessionStorage.setItem('edad', data.edad);

            window.location.href = "/perfil.html"; // Redirige a la página de perfil
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
        // Mostrar los datos del usuario en la página de perfil
        document.getElementById('nombreUsuario').innerText = sessionStorage.getItem('nombre') || "Invitado";
        document.getElementById('correoUsuario').innerText = sessionStorage.getItem('correo');
        document.getElementById('carreraUsuario').innerText = sessionStorage.getItem('carrera');
        document.getElementById('edadUsuario').innerText = sessionStorage.getItem('edad');
    }
});

// Función para cerrar sesión
function cerrarSesion() {
    sessionStorage.clear(); // Limpia los datos del usuario del sessionStorage
    window.location.href = "/index.html"; // Redirige a la página principal
}


