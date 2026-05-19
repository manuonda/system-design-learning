## ¿Qué es Picacku?

Picacku es una herramienta sencilla que facilita la gestión de imágenes de forma rápida y sencilla. Es ideal para principiantes que desean integrar imágenes en sus proyectos sin complicaciones.

## ¿Por qué usar Picacku?

- Fácil de usar.
- No requiere conocimientos avanzados.
- Rápido para proyectos pequeños y medianos.

## Instalación

Para comenzar, instala Picacku ejecutando el siguiente comando en tu terminal:

```bash
pip install picacku
```

## Primer ejemplo

Este ejemplo básico muestra cómo cargar y visualizar una imagen:

```python
from picacku import Picacku

# Crear una instancia con la ruta de la imagen
img = Picacku('ruta/a/tu/imagen.jpg')

# Mostrar la imagen
img.show()
```

El código anterior carga una imagen desde una ruta específica y la muestra en pantalla.

## Cambiar tamaño

Puedes redimensionar la imagen fácilmente:

```python
# Cambiar tamaño a 100x100 píxeles
i mg.resize(100, 100)
img.show()
```

## Guardar la imagen

Guarda la imagen modificada ejecutando:

```python
img.save('ruta/a/tu/nueva_imagen.jpg')
```

## Conclusión

Picacku es una herramienta efectiva y fácil de usar para quienes inician en el manejo de imágenes con Python. Su simplicidad y funcionalidad la convierten en una opción práctica para tareas básicas.

¡Prueba Picacku y comienza a trabajar con imágenes hoy mismo!