// La lista inicial se renderiza en el servidor mediante Thymeleaf.
function guardarRutaImagen(imagePath) {
	document.getElementById("fondo").value = imagePath;
	console.log(document.getElementById("fondo").value);
}

function eliminarProyecto(idProyecto){
	$.ajax({
		url : "/eliminarProyecto",
		contentType:"application/json",
		data : JSON.stringify({id_proyecto: idProyecto}),
		type : "POST",
		success: function(){
			window.location.href = 'inicio';
		}
	});
    
}

function obtenerInfo(datos, objetivo, nombre) {
	var nuevoTexto = "¿Esta seguro que sea eliminar el proyecto " + nombre + "?";
	document.getElementById("textAlerta").innerText = nuevoTexto;
    $(`#${objetivo}`).val(datos);
}

function botonEliminar(){
	
	var id = document.getElementById("hiddenId").value;
	eliminarProyecto(id);
}

function actualizarProyecto(idProyecto){
	$.ajax({
		url : "/actualizarProyecto",
		dataType: "json",
		contentType:'application/json',
		data : JSON.stringify({id_proyecto: idProyecto}),
		type : "POST",
		success: function(data){
			$('#modalEditar').modal('show');
			
			var modal = $('#modalEditar');
			modal.find('.modal-title').text('Editar')
				
			document.getElementById("id").value = data.id_proyecto;
			document.getElementById("editarNombre").value = data.nombre;
			document.getElementById("editarDescripcion").value = data.descripcion;
			document.getElementById("editar_fecha_inicio").value = data.fecha_inicio;
			document.getElementById("editar_fecha_finalizacion").value = data.fecha_finalizacion;
			document.getElementById("editar_estado_proyecto").value = data.estado_proyecto;
			document.getElementById("editar_fondo").value = data.fondo;
			console.log(document.getElementById("editar_fondo").value);
		
		},
		
		error: function(xhr) {
            mostrarErrorProyecto(xhr.responseJSON?.error || "No se pudo consultar el proyecto.");
        }
	});
}

function asignarFondo(){
	console.log(document.getElementById("fondo").value);
	if(document.getElementById("fondo").value == ""){
		document.getElementById("fondo").value = "/Images/blanco.png"; 
	}   
}

function asignarFondoEditar(){
	console.log(document.getElementById("editarFondo").value);
	if(document.getElementById("editarFondo").value == ""){
		document.getElementById("editarFondo").value = "/Images/blanco.png";
	}
}

function cerrarFondos() {
    const dialog = document.getElementById('imagenesFondo');
    if (dialog) {
        dialog.close();
    }
}
/*function actualizarProyecto(idProyecto){
	
	$.ajax({
		url : "/actualizarProyecto",
		contentType:"application/json",
		data : JSON.stringify({id_proyecto: idProyecto}),
		type : "GET",
		success: function(){
			var miModal = document.getElementById('modalEditar');

			// Crear un nuevo objeto Modal con el modal obtenido
			var modal = new bootstrap.Modal(miModal);
			
			// Abrir el modal
			modal.show();

		}
	});
	
}*/

/*function guardarRutaImagen(ruta){
	 document.getElementById("fondo").value = ruta;
	 console.log(ruta);
	 console.log("esta es la ruta " + document.getElementById("fondo").value)
}*/
function mostrarErrorProyecto(mensaje) {
    let alerta = document.getElementById('errorProyecto');
    if (!alerta) {
        alerta = document.createElement('div');
        alerta.id = 'errorProyecto';
        alerta.className = 'alert alert-danger';
        alerta.setAttribute('role', 'alert');
        document.getElementById('titulo').parentElement.after(alerta);
    }
    alerta.textContent = mensaje;
}