-- Create schema if not exists
CREATE SCHEMA IF NOT EXISTS ${app.schema-name};

-- Users
CREATE TABLE IF NOT EXISTS ${app.schema-name}.users (
  id           BIGSERIAL PRIMARY KEY,
  email        VARCHAR(255) UNIQUE NOT NULL,
  name VARCHAR(255),
  mobile_number VARCHAR(20),
  dob DATE,
  gender VARCHAR(50),
  is_email_verified BOOLEAN  NOT NULL DEFAULT false,
  password_hash VARCHAR(255)      NOT NULL,
  role         VARCHAR(50)        NOT NULL,
  created_at   TIMESTAMPTZ        NOT NULL DEFAULT NOW()
);

-- Sessions (store active JWTs)
CREATE TABLE IF NOT EXISTS ${app.schema-name}.sessions (
  id          BIGSERIAL PRIMARY KEY,
  user_id     BIGINT REFERENCES ${app.schema-name}.users(id) ON DELETE CASCADE,
  token       TEXT UNIQUE NOT NULL,
  created_at  TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  expires_at  TIMESTAMPTZ NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_sessions_user_created
  ON ${app.schema-name}.sessions(user_id, created_at);


-- Email Verification Tokens
CREATE TABLE IF NOT EXISTS ${app.schema-name}.email_verification_tokens (
  id          BIGSERIAL PRIMARY KEY,
  user_id     BIGINT REFERENCES ${app.schema-name}.users(id) ON DELETE CASCADE,
  token       VARCHAR(255) UNIQUE NOT NULL,
  expires_at  TIMESTAMPTZ NOT NULL,
  created_at  TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_email_verification_user
  ON ${app.schema-name}.email_verification_tokens(user_id);



CREATE TABLE IF NOT EXISTS ${app.schema-name}.jobs (
    id SERIAL PRIMARY KEY,
    title VARCHAR(255),
    description TEXT,
    requirements TEXT,
    salary NUMERIC,
    provider_id INT REFERENCES ${app.schema-name}.users(id),
    latitude DOUBLE PRECISION NOT NULL,
    longitude DOUBLE PRECISION NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);




