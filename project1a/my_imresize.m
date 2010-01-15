function [ B ] = my_imresize( A, m, n )
A = my_conv2(A, my_gauss(5, 5));

[ar ac dims] = size(A);

B = zeros(m, n, dims);

sr = m / ar;
sc = n / ac;

for d = 1:dims
  for i = 1:m
    si = i / sr;
    for j = 1:n
      sj = j / sc;
      
      dx = si - floor(si);
      dy = sj - floor(sj);
      
      si = enforce_bounds(si, ar);
      sj = enforce_bounds(sj, ac);
      
      B(i, j, d) = (1 - dx) * (1 - dy) * A(si, sj, d)...
                 + dx       * (1 - dy) * A(si + 1, sj, d)...
                 + (1 - dx) * dy       * A(si, sj + 1, d)...
                 + dx       * dy       * A(si + 1, sj + 1, d);
    end
  end
end

end

function [ r ] = enforce_bounds(n, m)
r = floor(n);
if r < 1
  r = 1;
elseif n >= m
  r = r - 1;
end
end