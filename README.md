# Agenda App #

Aplicativo Android para organizar a agenda de compromissos do usuário.

### Telas ###

![agenda_screens.png](https://bytebucket.org/frbentes/agendaac/raw/4bfccb46a5f892715ec238ed373acfa1931a2c2d/assets/agenda_screens.png?token=25af250575227eccdbc9c83a916674063f535cc5)

### Sobre ###

O desafio consistiu na criação de um aplicativo que gerenciasse as informações de uma agenda de compromissos, tendo como pré-requisito a autenticação do usuário. Tanto para a autenticação quanto para o armazenamento e sincronização dos dados na nuvem foi utilizado o [Firebase](https://firebase.google.com/). Para notificar o usuário com lembretes de um compromisso e permitir ajustes diversos como o tempo de antecedência do alerta, o aplicativo se integra com o Google Agenda (Calendar).    

### Requisitos ###

* Smartphone Android rodando o Google Play Services 9.0.0 ou superior
* Google Play Services instalado no Android SDK Manager
* Android Studio 1.5 ou superior
* App Google Agenda

### Dependências ###
* Firebase Auth
* Firebase Realtime Database
* Calligraphy

### Database Rules ###

```javascript
{
  "rules": {
    // User profiles are only readable/writable by the user who owns it.
    "users": {
      "$UID": {
        ".read": "auth.uid == $UID",
        ".write": "auth.uid == $UID"
      }
    },
    // Global appointments can be read by anyone but only written by logged-in users.
    "appointments": {
      ".read": true,
      ".write": "auth.uid != null",
      "$APPOINTMENTID": {
        // UID must match logged in user
        "uid": {
          ".validate": "newData.val() == auth.uid"
        }
      }
    },
    // User appointments are only writable by the user that owns it.
    "user-appointments": {
      ".read": true,
      "$UID": {
        "$APPOINTMENTID": {
          ".write": "auth.uid == $UID",
        	".validate": "data.exists() || newData.child('uid').val() == auth.uid"
        }
      }
    }
  }
}
```
### Instalação ###

* Clone o projeto do github:
   ```
   git clone https://github.com/frbentes/Agenda.git
   ```
* Importe o projeto no Android Studio: **File > New > Import Project**. Ou na tela de boas-vindas selecione **Import project**.

### Contato ###

* Fredyson Costa ([@frbentes](https://github.com/frbentes))
