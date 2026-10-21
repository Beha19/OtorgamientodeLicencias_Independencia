[1mdiff --git a/src/main/resources/templates/expedientes/mis.html b/src/main/resources/templates/expedientes/mis.html[m
[1mnew file mode 100644[m
[1mindex 0000000..eb053b6[m
[1m--- /dev/null[m
[1m+++ b/src/main/resources/templates/expedientes/mis.html[m
[36m@@ -0,0 +1,60 @@[m
[32m+[m[32m<!DOCTYPE html>[m
[32m+[m[32m<html lang="es" xmlns:th="http://www.thymeleaf.org">[m
[32m+[m[32m<head th:replace="~{fragments/layout :: head('Mis expedientes - LicenControl')}"></head>[m
[32m+[m
[32m+[m[32m<body class="app-body">[m
[32m+[m	[32m<div class="d-flex">[m
[32m+[m		[32m<aside th:replace="~{fragments/layout :: sidebar('expedientes')}"></aside>[m
[32m+[m
[32m+[m		[32m<div class="flex-grow-1 d-flex flex-column" style="min-width:0">[m
[32m+[m			[32m<header th:replace="~{fragments/layout :: encabezado}"></header>[m
[32m+[m
[32m+[m			[32m<main class="flex-grow-1">[m
[32m+[m				[32m<div class="panel">[m
[32m+[m					[32m<div class="panel-titulo">[m
[32m+[m						[32m<h2><i class="bi bi-folder"></i>Mis expedientes</h2>[m
[32m+[m					[32m</div>[m
[32m+[m
[32m+[m					[32m<div class="table-responsive">[m
[32m+[m						[32m<table class="table table-striped table-bordered align-middle">[m
[32m+[m							[32m<thead>[m
[32m+[m								[32m<tr>[m
[32m+[m									[32m<th>N.° de expediente</th>[m
[32m+[m									[32m<th>Nombre comercial</th>[m
[32m+[m									[32m<th>Giro</th>[m
[32m+[m									[32m<th>Estado</th>[m
[32m+[m									[32m<th>Fecha de registro</th>[m
[32m+[m									[32m<th>Fecha límite</th>[m
[32m+[m								[32m</tr>[m
[32m+[m							[32m</thead>[m
[32m+[m							[32m<tbody>[m
[32m+[m								[32m<tr th:each="e : ${expedientes}">[m
[32m+[m									[32m<td th:text="${e.numero} ?: '—'"></td>[m
[32m+[m									[32m<td th:text="${e.nombreComercial}"></td>[m
[32m+[m									[32m<td th:text="${e.giro.nombre}"></td>[m
[32m+[m									[32m<td>[m
[32m+[m										[32m<span class="badge" th:text="${e.estado}"[m
[32m+[m											[32mth:classappend="${e.estado == 'APROBADO' ? 'bg-success'[m
[32m+[m												[32m: (e.estado == 'DENEGADO' ? 'bg-danger'[m
[32m+[m												[32m: (e.estado == 'OBSERVADO' ? 'bg-warning text-dark'[m
[32m+[m												[32m: (e.estado == 'BORRADOR' or e.estado == 'PAGADO_SIN_ENVIO' ? 'bg-secondary' : 'bg-info text-dark')))}"></span>[m
[32m+[m									[32m</td>[m
[32m+[m									[32m<td th:text="${#temporals.format(e.fechaRegistro, 'dd/MM/yyyy HH:mm')}"></td>[m
[32m+[m									[32m<td th:text="${e.fechaLimite != null} ? ${#temporals.format(e.fechaLimite, 'dd/MM/yyyy')} : '—'"></td>[m
[32m+[m								[32m</tr>[m
[32m+[m								[32m<tr th:if="${#lists.isEmpty(expedientes)}">[m
[32m+[m									[32m<td colspan="6" class="text-center text-muted">No tienes expedientes registrados.</td>[m
[32m+[m								[32m</tr>[m
[32m+[m							[32m</tbody>[m
[32m+[m						[32m</table>[m
[32m+[m					[32m</div>[m
[32m+[m				[32m</div>[m
[32m+[m			[32m</main>[m
[32m+[m
[32m+[m			[32m<footer th:replace="~{fragments/layout :: pie}"></footer>[m
[32m+[m		[32m</div>[m
[32m+[m	[32m</div>[m
[32m+[m
[32m+[m	[32m<th:block th:replace="~{fragments/layout :: scripts}"></th:block>[m
[32m+[m[32m</body>[m
[32m+[m[32m</html>[m
\ No newline at end of file[m
