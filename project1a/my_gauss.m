function [ H ] = my_gauss( k, sigma )
kh = floor(k / 2);
M = exp(-((-kh:kh) .^ 2 / (2 * sigma ^ 2)));
H = M' * M;
H = 1 / sum(sum(H)) * H; % The continuous function doesn't 
                         % scale this correctly.
end

