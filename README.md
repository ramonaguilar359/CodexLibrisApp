# CodexLibris

**CodexLibris** és una aplicació mòbil per Android que implementa autenticació mitjançant JWT, integració amb una API REST i diverses pantalles (Splash Screen, Login i Menú Principal). La interfície s'adapta segons el rol de l'usuari (administrador o usuari normal) i permet fer logout per finalitzar la sessió.

---

## Funcionalitats

- **Splash Screen:**  
  Mostra una pantalla d'inici durant uns segons abans de redirigir a la pantalla de login.

- **Login:**  
  Permet a l'usuari introduir el seu nom d'usuari i contrasenya.  
  - Si el login és exitós, es guarden el token i el `roleId` a SharedPreferences.  
  - Si el login falla, es mostra un missatge d'error.

- **Menú Principal:**  
  Mostra opcions fictícies (Opció 1, Opció 2, …, Opció 5).  
  - Si el `roleId` és 1 (administrador), es mostra una opció addicional exclusiva per a administradors.  
  - Inclou un botó de logout que esborra el token i el `roleId` i redirigeix a la pantalla de login.

- **Logout:**  
  Elimina les dades de sessió i torna a la pantalla de login.

- **Integració amb API REST:**  
  Utilitza Retrofit per realitzar les peticions HTTP a la API.  
  La URL base es defineix com `http://10.0.2.2:8080`, perquè l'emulador d'Android utilitza aquesta adreça per accedir al localhost de la màquina host.

- **Proves:**  
  - **Proves Unitàries:** Validació de la lògica de login (login exitós, login erroni per contrasenya, login amb camps buits) mitjançant mocks.  
  - **Proves d'Integració:** Proves que realitzen connexions reals a l'API per validar el comportament del login.

---

## Estructura del Projecte

```bash
  CodexLibris/ 
  ├── app/ 
  │ ├── src/ 
  │ │ ├── main/ 
  │ │ │ ├── java/com/example/codexlibris/ 
  │ │ │ │ ├── SplashActivity.java // Pantalla Splash 
  │ │ │ │ ├── MainActivity.java // Pantalla de Login 
  │ │ │ │ ├── MainMenuActivity.java // Menú Principal 
  │ │ │ │ ├── RetrofitClient.java // Configuració de Retrofit 
  │ │ │ │ ├── ApiService.java // Interfície de l'API REST 
  │ │ │ │ ├── LoginRequest.java // Model de petició de login 
  │ │ │ │ └── LoginResponse.java // Model de resposta de login 
  │ │ │ └── res/ 
  │ │ │ ├── layout/ 
  │ │ │ │ ├── activity_splash.xml // Layout de la Splash Screen 
  │ │ │ │ ├── activity_main.xml // Layout del Login 
  │ │ │ │ └── activity_main_menu.xml // Layout del Menú Principal 
  │ │ │ └── values/ 
  │ │ │ └── colors.xml // Definició de colors 
  │ │ └── androidTest/ // Proves d'Integració 
  │ └── build.gradle 
  └── README.md
```
## Configuració i Execució

1. **Clonar el repositori:**

   ```bash
   git clone https://github.com/el-teu-usuari/codexlibris.git
   cd codexlibris

2. **Obrir el projecte a Android Studio:**
  - Importa el projecte i assegura’t que totes les dependències estan configurades correctament.

3. **Configuració del servidor:**

  - Assegura’t que el backend estigui en funcionament i accessible a través de http://10.0.2.2:8080.
  - Si el backend s’executa amb Docker, comprova que el contenidor estigui actiu.

4. **Execució de l’aplicació:**

  - L’aplicació començarà amb la Splash Screen.
  - Es mostrarà la pantalla de Login, on podràs introduir les teves credencials.
  - Un cop autenticat, es redirigeix al Menú Principal, on es mostren opcions diferents segons el rol de l'usuari.
  - El botó de Logout esborra la sessió i torna a la pantalla de Login.

5. **Execució de les proves:**

  - Proves unitàries: S’executen des de app/src/test/java/ amb JUnit i Mockito.
  - Proves d’integració: S’executen des de app/src/androidTest/java/ i realitzen connexions reals a l’API.

---

## Dependències
  - *Retrofit 2*: Per gestionar les peticions HTTP i la conversió de JSON.
  - *Gson Converter*: Per convertir entre JSON i objectes Java.
  - *AndroidX*: Per a la gestió de components i proves instrumentades.
  - *JUnit i Mockito*: Per a les proves unitàries.
  - *Espresso*: Per a les proves d'interfície d'usuari (UI).

---

## Notes Addicionals
**URL Base en RetrofitClient:**
Es defineix com http://10.0.2.2:8080 perquè l'emulador d'Android utilitza aquesta adreça per accedir al localhost de la màquina host.

**Adaptació de la UI:**
La interfície del Menú Principal s'adapta segons el roleId. Si el roleId és 1, es mostren opcions addicionals per a administradors.

## Autor
Ramon Aguilar
