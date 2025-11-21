import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description "Should change account info"
    request {
        method PATCH()
        url "/account/info"
        headers {
            contentType(applicationJson())
        }
        body([
                login      : "login",
                firstName  : "Тест",
                lastName   : "Тестов",
                "birthDate": "1990-01-01"
        ])
    }
    response {
        status OK()
    }
}