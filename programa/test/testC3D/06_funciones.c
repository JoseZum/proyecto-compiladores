| TEST C3D: Declaración y llamada de funciones |
| Debe generar código de 3 direcciones con llamadas a funciones |

gift int suma ¿int a, int b? ¡
    local int resultado = a + b endl
    return resultado endl
!

gift int factorial ¿int n? ¡
    decide of
        n <= 1 => ¡
            return 1 endl
        !
        else => ¡
            local int temp = n - 1 endl
            local int fact = factorial¿temp? endl
            return n * fact endl
        !
    end decide endl
!

coal navidad ¿? ¡
    local int x = 5 endl
    local int y = 10 endl
    local int z = suma¿x, y? endl
    local int f = factorial¿5? endl
!
