package dharmaInventario.dharmaInventario;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DharmaInventarioApplication {

	public static void main(String[] args) {
		System.out.println("SPRING_DATASOURCE_URL = " + System.getenv("SPRING_DATASOURCE_URL"));
		SpringApplication.run(DharmaInventarioApplication.class, args);
        System.out.println("Levantando proyecto");
	}
}
