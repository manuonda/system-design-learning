# Conociendo a Pikachu: El Icono de Pokémon para Principiantes

Pikachu es uno de los Pokémon más famosos y emblemáticos. Si eres nuevo en el mundo Pokémon, Pikachu es un excelente punto de partida, ya que es el Pokémon eléctrico más reconocido.

## ¿Quién es Pikachu?

Pikachu es un pequeño ratón amarillo con mejillas rojas que puede generar electricidad para defenderse. Aparece en juegos, series animadas y películas de Pokémon.

## ¿Por qué Pikachu es especial?

- Fácil de reconocer.
- Representa la marca Pokémon.
- Posee habilidades eléctricas únicas.

## Código ejemplo: Crear un objeto Pikachu en Python

```python
class Pokemon:
    def __init__(self, nombre, tipo):
        self.nombre = nombre
        self.tipo = tipo

    def ataque(self):
        print(f"{self.nombre} ataca!")

class Pikachu(Pokemon):
    def __init__(self):
        super().__init__('Pikachu', 'Eléctrico')

    def ataque(self):
        print(f"{self.nombre} usa Rayo!")

# Crear un objeto Pikachu y ejecutar su ataque
mi_pikachu = Pikachu()
mi_pikachu.ataque()
```

Este código crea un objeto Pokémon llamado Pikachu y ejecuta su ataque eléctrico: Rayo.

## ¿Qué aprenderás jugando con Pikachu?

- Cómo funcionan los tipos de Pokémon.
- Conceptos básicos de ataque y defensa.
- Fundamentos de programación con ejemplos simples.

Pikachu es un gran compañero para iniciar tu aventura en el mundo de Pokémon y la programación. ¡Prueba a crear y modificar su código para hacerlo tuyo!

¡Diviértete aprendiendo sobre Pokémon y programación!