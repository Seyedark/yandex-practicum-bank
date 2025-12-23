import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description "Should change account balance"
    request {
        method PATCH()
        url "/account/balance"
        headers {
            contentType(applicationJson())
        }
        body([
                login  : "login",
                balance: 1,
                currency: "RUB"
        ])
    }
    response {
        status OK()
    }
}