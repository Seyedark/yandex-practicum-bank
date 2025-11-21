package contracts

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description "Should send email notifications"
    request {
        method POST()
        url "/notification/email"
        headers {
            contentType(applicationJson())
        }
        body("""
                [
                    {
                        "email": "email@mail.ru",
                        "message": "Тестовое сообщение"
                    },
                    {
                        "email": "email@mail.ru",
                        "message": "Тестовое сообщение"
                    }
                ]
                """)
    }
    response {
        status OK()
    }
}