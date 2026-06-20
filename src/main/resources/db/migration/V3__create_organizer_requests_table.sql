CREATE TABLE organizer_requests(
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    reason TEXT NOT NULL,
    status VARCHAR(20) NOT NULL,
    requested_at TIMESTAMP NOT NULL,
    reviewed_at TIMESTAMP,

    CONSTRAINT fk_organizer_request_user
        FOREIGN KEY (user_id)
        REFERENCES users(id)
);