-- Enable pg_net if not already active
create extension if not exists pg_net;
create extension if not exists vault;

-- Trigger the Edge Function once immediately to test data population
-- Note: Using the service role token provided for authentication
select
  net.http_post(
    url := 'https://qdamlmhappvpecixuqcb.supabase.co/functions/v1/process-market-alerts',
    headers := jsonb_build_object(
      'Content-Type', 'application/json',
      'Authorization', 'Bearer ' || vault.get_secret('SUPABASE_SERVICE_ROLE_KEY')
    )
  );
