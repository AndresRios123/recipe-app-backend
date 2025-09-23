[api_recetas_endpoints.md](https://github.com/user-attachments/files/22488477/api_recetas_endpoints.md)

# 📖 API Recetas – Endpoints

Este documento describe los endpoints disponibles en la API de **recetas** con su uso y ejemplos.

---

## 🔑 Autenticación (`/api/auth`)

### Registro de usuario
**POST** `http://localhost:8088/api/auth/register`

```json
{
  "username": "juan",
  "password": "Clave123",
  "email": "juan@example.com"
}
```

---

### Inicio de sesión
**POST** `http://localhost:8088/api/auth/login`

```json
{
  "username": "juan",
  "password": "Clave123"
}
```

---

### Verificar sesión
**GET** `http://localhost:8088/api/auth/status`  
➡️ Verifica si la cookie sigue activa.

---

### Perfil del usuario logueado
**GET** `http://localhost:8088/api/auth/profile`

---

### Cambiar contraseña
**POST** `http://localhost:8088/api/auth/change-password`

```json
{
  "currentPassword": "Clave123",
  "newPassword": "Clave456"
}
```

---

### Cerrar sesión
**POST** `http://localhost:8088/api/auth/logout`  
➡️ Invalida la sesión activa.

---

### Listar usuarios
**GET** `http://localhost:8088/api/auth/users`  
➡️ Obtiene todos los usuarios sin mostrar contraseñas.

---

### Listar usuarios protegidos
**GET** `http://localhost:8088/api/auth/users-protected`  
➡️ Igual que el anterior, pero responde **404** si la sesión caducó.

---

### Obtener un usuario específico
**GET** `http://localhost:8088/api/auth/user/{id}`  
➡️ Reemplaza **{id}** con el número del usuario.

---

## 👥 CRUD de Usuarios (`/api/users`)

> Estos endpoints requieren autenticación.

### Crear usuario
**POST** `http://localhost:8088/api/users`  

Usa el mismo JSON de registro:

```json
{
  "username": "nuevo_usuario",
  "password": "Clave123",
  "email": "nuevo@example.com"
}
```

---

### Listar usuarios
**GET** `http://localhost:8088/api/users`  
➡️ Devuelve entidades completas (campo `password` oculto con `@JsonIgnore`).

---

### Obtener un usuario por ID
**GET** `http://localhost:8088/api/users/{id}`  
➡️ Responde con 404 si no existe.

---

### Actualizar usuario
**PUT** `http://localhost:8088/api/users/{id}`  

```json
{
  "username": "juan_actualizado",
  "email": "juan.nuevo@example.com"
}
```

⚠️ La contraseña **no** se modifica aquí.

---

### Eliminar usuario
**DELETE** `http://localhost:8088/api/users/{id}`  
➡️ Elimina el usuario y devuelve un mensaje simple.

---

## 📌 Notas

- Todos los endpoints devuelven **JSON**.  
- Algunos requieren sesión activa para responder.  
- Si la sesión caducó, los endpoints protegidos devuelven **404**.
