# Introducción a Pokémon para Principiantes

Pokémon es una franquicia y un juego muy popular en el que los jugadores capturan y entrenan criaturas llamadas Pokémon. Aquí están algunos conceptos básicos para entender Pokémon de forma práctica.

## ¿Qué es un Pokémon?

Un Pokémon tiene un nombre, un tipo y puntos de salud (HP). Por ejemplo, Pikachu es un Pokémon de tipo Eléctrico.

## Código sencillo: crear un Pokémon

Veamos un ejemplo simple en JavaScript para crear un Pokémon:

```javascript
const pikachu = {
  nombre: 'Pikachu',
  tipo: 'Eléctrico',
  hp: 35
};

console.log(pikachu);
```

Este código crea un objeto `pikachu` con sus características básicas.

## Atacar con un movimiento

Un Pokémon puede usar movimientos para atacar. Aquí definimos un movimiento y lo usamos:

```javascript
const movimiento = {
  nombre: 'Impactrueno',
  poder: 40
};

function atacar(atacante, movimiento) {
  console.log(`${atacante.nombre} usa ${movimiento.nombre}!`);
}

atacar(pikachu, movimiento);
```

Esto imprime qué movimiento usa el Pokémon.

## Resumen

- Un Pokémon tiene un nombre, un tipo y HP.
- Puedes representarlo con objetos en código.
- Los movimientos son acciones que un Pokémon puede usar para atacar.

Con esta introducción básica, puedes empezar a explorar cómo programar conceptos simples de Pokémon. ¡Diviértete creando tus propios Pokémon y ataques!
