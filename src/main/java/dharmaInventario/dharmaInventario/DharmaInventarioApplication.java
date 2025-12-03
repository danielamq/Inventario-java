package dharmaInventario.dharmaInventario;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DharmaInventarioApplication {

	public static void main(String[] args) {
		SpringApplication.run(DharmaInventarioApplication.class, args);
        System.out.println("Levantando proyecto");
		System.out.println("SPRING_DATASOURCE_URL = " + System.getenv("SPRING_DATASOURCE_URL"));
		System.out.println("SPRING_DATASOURCE_USERNAME = " + System.getenv("SPRING_DATASOURCE_USERNAME"));
		System.out.println("SPRING_DATASOURCE_PASSWORD = " + System.getenv("SPRING_DATASOURCE_PASSWORD"));
	}
}
