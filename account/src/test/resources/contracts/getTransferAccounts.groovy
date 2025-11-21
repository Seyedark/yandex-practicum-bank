import org.springframework.cloud.contract.spec.Contract
Contract.make {
    description "Should get transfer accounts info"
    request {
        method GET()
        url("/account/transfer") {
            queryParameters {
                parameter("loginFrom", "login1")
                parameter("loginTo", "login2")
            }
        }
        headers {
            contentType(applicationJson())
        }
    }
    response {
        status OK()
        body([
                loginFrom: "login1",
                emailFrom: "login1@mail.ru",
                balanceFrom: 1,
                loginTo: "login2",
                emailTo: "login2@mail.ru",
                balanceTo: 1
        ])
        headers {
            contentType(applicationJson())
        }
    }
}