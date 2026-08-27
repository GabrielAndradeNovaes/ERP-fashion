import os

files_to_clean = {
    r"src\main\java\com\erp\core\controller\UsuarioController.java": ["import com.erp.core.domain.Empresa;", "import java.util.Set;"],
    r"src\main\java\com\erp\core\tenant\TenantProvisioningService.java": ["import org.springframework.transaction.annotation.Transactional;"],
    r"src\test\java\com\erp\core\controller\UsuarioControllerTest.java": ["import com.erp.core.domain.Empresa;", "import com.erp.core.security.UsuarioEmpresa;"],
    r"src\test\java\com\erp\core\security\JwtAuthenticationFilterTest.java": ["import org.springframework.security.core.authority.SimpleGrantedAuthority;"],
    r"src\test\java\com\erp\core\tenant\TenantProvisioningServiceTest.java": ["import com.erp.core.security.Usuario;", "import java.sql.SQLException;"],
    r"src\test\java\com\erp\inventory\controller\EstoqueProdutoControllerTest.java": ["import java.math.BigDecimal;"],
    r"src\test\java\com\erp\production\service\impl\FichaTecnicaServiceImplTest.java": ["import com.erp.production.domain.FichaTecnicaMaterial;"],
    r"src\test\java\com\erp\production\service\impl\PcpServiceImplTest.java": ["import java.time.LocalDateTime;"]
}

base_dir = r"c:\projetos\fashion-erp\meu-erp-confeccao\backend"

for rel_path, imports in files_to_clean.items():
    full_path = os.path.join(base_dir, rel_path)
    if os.path.exists(full_path):
        with open(full_path, 'r', encoding='utf-8') as f:
            lines = f.readlines()
        with open(full_path, 'w', encoding='utf-8') as f:
            for line in lines:
                if not any(imp in line for imp in imports):
                    f.write(line)

print("Imports cleaned.")
