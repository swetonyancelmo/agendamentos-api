# 📅 Refatoração Backend — Sistema de Agendamento (Nicho: Ar-Condicionado)

## 📌 Contexto

O sistema originalmente foi projetado para múltiplas empresas (multi-tenant), permitindo que várias empresas cadastrassem serviços e gerenciassem agendamentos.

Após mudança de escopo, o sistema agora será direcionado para **um único prestador de serviços (técnico de ar-condicionado)**.

---

## 🎯 Objetivo da Refatoração

Simplificar o backend para um **MVP funcional**, removendo complexidade desnecessária e focando em:

* Agendamento de serviços
* Gestão de clientes
* Controle de horários
* Fluxo simples e eficiente

---

# 🧠 Mudança de Arquitetura

## Antes (Genérico)

* Múltiplas empresas (`Business`)
* Serviços por empresa
* Autorização complexa
* Multi-tenant

## Agora (Nicho)

* Um único prestador
* Serviços fixos
* Estrutura simplificada
* Foco em agendamentos

---

# ✂️ Remoções

## ❌ Entidade: Business

Remover completamente:

* `/api/business`
* Qualquer uso de `businessId`
* Relacionamentos com empresa

---

## ❌ Services por empresa

Antes:

```
/api/business/services
```

Agora:

* Serviços são globais (do técnico)
* Não precisam de vínculo com empresa

---

## ❌ Availability vinculada a empresa

Antes:

```
/api/business/availability
```

Agora:

```
/api/availability
```

---

# ✅ Estrutura Final do MVP

## 🔐 Auth

```
POST /auth/login
POST /auth/customer/register
```

> Futuro: unificar com roles (ADMIN / CUSTOMER)

---

## 👤 Usuário

```
GET /api/me
```

---

## 🔧 Serviços

```
GET /api/services
POST /api/services        (ADMIN)
```

### Exemplo de serviços:

* Limpeza de ar-condicionado
* Instalação
* Manutenção

---

## 📅 Agendamentos

### Cliente

```
POST   /api/appointments
GET    /api/appointments/me
PATCH  /api/appointments/{id}/cancel
```

### Admin (técnico)

```
GET    /api/appointments
PATCH  /api/appointments/{id}/status
```

---

## 🕒 Disponibilidade

```
GET /api/availability
POST /api/availability    (opcional)
```

> Alternativa MVP: gerar horários automaticamente (ex: 08h às 18h)

---

# 🚀 Novas Funcionalidades

## 📍 Endereço do Cliente

Adicionar ao agendamento:

```
address
city
reference
```

---

## 📊 Status do Agendamento

Criar enum:

```java
PENDING,
CONFIRMED,
CANCELLED,
COMPLETED
```

---

# 🧱 Modelagem de Entidades

## 👤 User

```java
class User {
    Long id;
    String name;
    String email;
    String password;
    Role role; // ADMIN ou CUSTOMER
}
```

---

## 🔧 Service

```java
class Service {
    Long id;
    String name;
    String description;
    BigDecimal price;
    Integer durationMinutes;
}
```

---

## 📅 Appointment

```java
class Appointment {
    Long id;
    User customer;
    Service service;
    LocalDateTime dateTime;
    AppointmentStatus status;
    String address;
    String city;
    String reference;
    String notes;
}
```

---

# ⚙️ Regras de Negócio (IMPORTANTE)

* ❌ Não permitir agendamento em horários ocupados
* ❌ Não permitir agendamento no passado
* ✔️ Validar duração do serviço
* ✔️ Garantir que horário esteja dentro da disponibilidade
* ✔️ Cliente só pode ver seus próprios agendamentos

---

# ⚠️ Decisão Técnica Importante

## NÃO manter multi-empresa "por garantia"

Evitar:

> “vamos deixar preparado para múltiplas empresas no futuro”

Problemas:

* Aumenta complexidade
* Dificulta manutenção
* Atrapalha entrega do MVP

✔️ Solução:

* Focar no cenário atual
* Refatorar no futuro se necessário

---

# 💡 Visão de Produto

Este sistema agora é:

> Um gerenciador de agenda para técnico de ar-condicionado

Possíveis evoluções:

* Painel administrativo
* Notificações (WhatsApp / Email)
* Integração com pagamentos
* App mobile

---

## 🔄 FASE 3 — Fluxo de Aprovação de Agendamentos

### 📌 Objetivo

Implementar controle de aprovação de agendamentos pelo técnico (ADMIN), permitindo:

* Aceitar agendamentos
* Rejeitar agendamentos com justificativa
* Cancelamento pelo cliente

---

## 🚀 Endpoints

### ✔️ Aceitar agendamento (ADMIN)

```
PUT /api/appointments/{id}/accept
```

* Altera status para: `CONFIRMED`

---

### ❌ Rejeitar agendamento (ADMIN)

```
PUT /api/appointments/{id}/reject
```

* Altera status para: `CANCELLED`
* Deve receber um motivo da rejeição

#### Exemplo de body:

```json
{
  "reason": "Horário indisponível"
}
```

---

### 🚫 Cancelar agendamento (CLIENTE)

```
PUT /api/appointments/{id}/cancel
```

* Altera status para: `CANCELLED`

---

## 📊 Atualização do Enum de Status

```java
PENDING,     // aguardando aprovação
CONFIRMED,   // aceito pelo técnico
CANCELLED,   // cancelado ou rejeitado
COMPLETED    // serviço finalizado
```

---

## ⚙️ Regras de Negócio

### 🔐 Permissões

* Apenas **ADMIN (técnico)** pode:

  * Aceitar agendamentos
  * Rejeitar agendamentos

* Apenas **CLIENTE dono do agendamento** pode:

  * Cancelar seu próprio agendamento

---

### 📌 Validações obrigatórias

* ❌ Não permitir aceitar/rejeitar agendamentos já finalizados
* ❌ Não permitir alterar status de agendamento já cancelado
* ❌ Não permitir cliente cancelar agendamento de outro cliente
* ✔️ Validar se o agendamento pertence ao usuário autenticado
* ✔️ Validar se o status atual é `PENDING` antes de aceitar/rejeitar

---

## 💡 Observações Técnicas

* O campo `reason` pode ser armazenado em:

  ```java
  String cancellationReason;
  ```

* Ideal registrar:

  * Data da alteração de status
  * Quem realizou a ação (opcional para MVP)

---

## 🧠 Benefícios dessa Feature

* Controle real do fluxo de atendimento
* Evita conflitos de agenda
* Melhora experiência do cliente
* Aproxima o sistema de um produto real

---


# 🏁 Conclusão

A refatoração transforma o sistema de:

❌ Genérico e complexo
➡️
✅ Simples, focado e funcional

Isso permite:

* Entrega mais rápida
* Código mais limpo
* Melhor experiência para o usuário

---

# 🚀 Próximos Passos

* Implementar novas entidades
* Refatorar controllers
* Ajustar DTOs
* Criar validações
* Testar fluxo completo de agendamento

---

