package mediaManager.message

import org.springframework.data.repository.CrudRepository

interface MessageRepository : CrudRepository<Message, Integer>
