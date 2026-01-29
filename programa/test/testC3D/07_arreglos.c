| TEST C3D: Declaración y acceso a arreglos |
| Debe generar código de 3 direcciones con acceso a arreglos |

coal navidad ¿? ¡
    local int matriz@3A@3A endl
    
    matriz@0A@0A = 1 endl
    matriz@0A@1A = 2 endl
    matriz@0A@2A = 3 endl
    matriz@1A@0A = 4 endl
    matriz@1A@1A = 5 endl
    matriz@1A@2A = 6 endl
    
    local int suma = matriz@0A@0A + matriz@1A@1A endl
    local int valor = matriz@0A@1A endl
    
    for ¿int i = 0 endl i < 3 endl ++i? ¡
        matriz@iA@iA = i * 2 endl
    ! endl
!
