import org.springframework.cloud.contract.spec.Contract
Contract.make {
    description "Should get balance"
    request {
        method GET()
        url("/account/balance") {
            queryParameters {
                parameter("login", "login")
                parameter("currency", "RUB")
            }
        }
    }
    response {
        status OK()
        body([
                email: "login@mail.ru",
                balance: 1
        ])
        headers {
            contentType(applicationJson())
        }
    }
}